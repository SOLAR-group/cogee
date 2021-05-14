package uk.ac.ucl.cs.solar.cogee;

import org.apache.log4j.Logger;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import uk.ac.ucl.cs.solar.cogee.algorithm.CoGEEFactory;
import uk.ac.ucl.cs.solar.cogee.dataset.Dataset;
import uk.ac.ucl.cs.solar.cogee.dataset.DatasetFileReaderFactory;
import uk.ac.ucl.cs.solar.cogee.dataset.EffortEstimationFold;
import uk.ac.ucl.cs.solar.cogee.evaluation.Evaluator;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.ioservice.*;
import uk.ac.ucl.cs.solar.cogee.problem.SEEProblem;
import uk.ac.ucl.cs.solar.cogee.result.ExperimentEvaluationResult;
import uk.ac.ucl.cs.solar.cogee.result.ParetoFrontResult;
import uk.ac.ucl.cs.solar.cogee.result.RunEvaluationResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoGEERunner {

    static Logger logger = Logger.getLogger(CoGEERunner.class);

    public static void main(String[] args) {
        try {
            CoGEEArgs coGEEArgs = new CoGEEArgs(args);
            FileNameHandler.setRootPath(coGEEArgs.getRoot());
            CoGEERunner.run(coGEEArgs);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public static void run(CoGEEArgs args) {
        try {

            String dataset = args.getDataset();

            //reading the config file. This file contains the settings of algorithms to run. (Please refer to README.md
            // for more information)
            List<GAConfigObject> configs = FileNameHandler.readConfigsFile();

            for (GAConfigObject conf : configs) {

                //The file name handler is responsible for creating files and giving out customized output file names
                // for different algorithms and data sets.
                FileNameHandler fileHandler = new FileNameHandler(conf, dataset);

                String trainFileName = fileHandler.getTrainFileName();
                String testFileName = fileHandler.getTestFileName();

                //An interface for reading data set files. The data set files content need to be compliant with the
                // explanations  in the README.md file.
                DatasetFileReader excelReader =
                        DatasetFileReaderFactory.getReader(FileNameHandler.getFileType(trainFileName));
                boolean cogee = args.getFunction().toLowerCase().contains("cogee");
                boolean eval = args.getFunction().toLowerCase().contains("eval");
                boolean pred = args.getFunction().toLowerCase().contains("pred");
                try {
                    Dataset<EffortEstimationFold> trainSet = excelReader.read(new File(trainFileName));
                    Dataset<EffortEstimationFold> testSet = excelReader.read(new File(testFileName));
                    if (cogee)
                        runExperiment(conf, fileHandler, trainSet);
                    if (pred)
                        predict(conf, fileHandler, testSet);
                    if (eval)
                        evaluate(conf, fileHandler, testSet);
                } catch (CogeeException e) {
                    logger.error(e);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    //Executes a number of runs on a training set and produces Pareto Front and Solution (Chromosome Variables) files
    // and store them under a folder specific for the input configuration.
    private static void runExperiment(
            GAConfigObject conf,
            FileNameHandler fileHandler,
            Dataset<EffortEstimationFold> trainSet
    ) {
        try {
            logger.info("Start Running CoGEE...");
            for (int run = 1; run <= conf.getNumberOfRuns(); run++) {
                Long overallRunTime = 0L;

                for (EffortEstimationFold fold : trainSet.getFolds()) {
                    Long singleRuntime = 0L;
                    SEEProblem problem = new SEEProblem(conf.getObjectives(), fold);

                    CoGEEFactory cogee = new CoGEEFactory().prepareInstance(problem, conf);

                    JMetalRandom.getInstance().setSeed(System.currentTimeMillis());

                    Long startMils = System.currentTimeMillis();
                    cogee.run();
                    Long endMils = System.currentTimeMillis();

                    singleRuntime += endMils - startMils;
                    overallRunTime += singleRuntime;

                    List<DoubleSolution> rankOneFront = cogee.getResult();

                    ResultWriter.writeParetoFront(rankOneFront, fileHandler.getFrontFileName(run, fold.getName()));
                    ResultWriter.writeSolutions(rankOneFront, fileHandler.getSolutionsFileName(run, fold.getName()));

                }
                logger.info("Run number " + (run) + " is done! time: " + overallRunTime);
            }
            logger.info("CoGEE is Done!");
        } catch (CogeeException cex) {
            cex.printStackTrace();
            logger.error(cex.getMessage(), cex);
        }
    }

    //Evaluates the models produced by an experiment on the test set, and produces output files:
    // 1) A file containing the results of the evaluation per project in each run,reporting the Mean Absolute Error (MAE)
    //    and the Median Absolute Error (MdAE) of the prediction for each Project in each Run.
    // 2) A file containing the results of the evaluation per run, reporting Mean/Median MAE, Mean/Median MdAE, and CI
    //    of the predictions made by models produced by each run on the test set.
    private static void evaluate(GAConfigObject conf,
                                 FileNameHandler fileHandler,
                                 Dataset<EffortEstimationFold> testSet
    ) {
        try {
            logger.info("Starting to Evaluate the Test set...");
            logger.info("Dataset: " + testSet.getName());
            ExperimentEvaluationResult experimentEvaluationResults = new ExperimentEvaluationResult(conf.getNumberOfRuns());

            List<Object> perProjectResultHeader = new ArrayList<>();
            perProjectResultHeader.add("Run");
            perProjectResultHeader.add("ProjectID");
            perProjectResultHeader.add(conf.getAlgorithm() + "-MeanAE");
            perProjectResultHeader.add(conf.getAlgorithm() + "-MedianAE");

            List<List<Object>> aggregatedFoldsMetrics = new ArrayList<>();

            for (int run = 1; run <= conf.getNumberOfRuns(); run++) {
                RunEvaluationResult expResult = new RunEvaluationResult(run);
                List<Double> mae = new ArrayList<>();
                List<Double> mdae = new ArrayList<>();
                for (EffortEstimationFold fold : testSet.getFolds()) {
                    SEEProblem problem = new SEEProblem(conf.getObjectives(), fold);
                    List<DoubleSolution> front =
                            ResultReader.readFront(fileHandler.getSolutionsFileName(run, fold.getName()), problem);
                    //returns a table of residuals and last two columns are meanAE and medianAE
                    Map<Integer, ParetoFrontResult> pfResults =
                            Evaluator.getParetoFrontResults(conf.getObjectives(), front, fold);
                    List<String> predictions = new ArrayList<>();
                    //Prediction Results for the current fold of the test set
                    for (ParetoFrontResult paretoFrontResult : pfResults.values()) {
                        List<Object> metricRow = new ArrayList<>();
                        metricRow.add(Integer.valueOf(run));
                        metricRow.add(paretoFrontResult.getProjectId());
                        metricRow.add(paretoFrontResult.getMeanAE());
                        metricRow.add(paretoFrontResult.getMedianAE());
                        mae.add(paretoFrontResult.getMeanAE());
                        mdae.add(paretoFrontResult.getMedianAE());
                        aggregatedFoldsMetrics.add(metricRow);
                    }
                }
                expResult.setMeanMAE(Evaluator.getMeanForList(mae));
                expResult.setMedianMAE(Evaluator.getMedianForList(mae));
                expResult.setMeanMdAE(Evaluator.getMeanForList(mdae));
                expResult.setMedianMdAE(Evaluator.getMedianForList(mdae));
                expResult.setCI(Evaluator.getCIForList(mae));
                experimentEvaluationResults.putRunEvaluationResult(expResult);
            }
            TextWriter.write(
                    aggregatedFoldsMetrics,
                    fileHandler.getPerProjectResultFileName(conf.getNumberOfRuns(), FileType.CSV),
                    perProjectResultHeader);
            TextWriter.write(
                    experimentEvaluationResults.toTable(),
                    fileHandler.getResultFileName(conf.getNumberOfRuns(), FileType.CSV),
                    experimentEvaluationResults.getHeader());

            logger.info("Evaluation is Done!");
        } catch (CogeeException cex) {
            cex.printStackTrace();
            logger.error(cex.getMessage(), cex);
        }
    }

    //Predicts and prints all the predicted effort values for projects in the test set, using all of the prediction
    // models (i.e. Chromosomes) in the Pareto Front of the run.
    private static void predict(GAConfigObject conf,
                                FileNameHandler fileHandler,
                                Dataset<EffortEstimationFold> testSet) {
        try {
            logger.info("Starting to Predict...");
            logger.info("Dataset: " + testSet.getName());

            for (int run = 1; run <= conf.getNumberOfRuns(); run++) {
                for (EffortEstimationFold fold : testSet.getFolds()) {
                    SEEProblem problem = new SEEProblem(conf.getObjectives(), fold);
                    List<DoubleSolution> front =
                            ResultReader.readFront(fileHandler.getSolutionsFileName(run, fold.getName()), problem);
                    //returns a table of residuals and last two columns are meanAE and medianAE
                    Map<Integer, ParetoFrontResult> pfResults =
                            Evaluator.getParetoFrontResults(conf.getObjectives(), front, fold);
                    List<String> predictions = new ArrayList<>();
                    //Prediction Results for the current fold of the test set
                    for (ParetoFrontResult paretoFrontResult : pfResults.values()) {
                        predictions.add(paretoFrontResult.getProjectId() + fileHandler.SEPARATOR +
                                paretoFrontResult.getActualEffort() + fileHandler.SEPARATOR +
                                paretoFrontResult.printPredictions());
                    }
                    if (!predictions.isEmpty()) {
                        StringBuilder header = new StringBuilder("ProjectID" + fileHandler.SEPARATOR +
                                "ActualEffort" + fileHandler.SEPARATOR);
                        for (int i = 1; i <= front.size(); i++)
                            header.append("Chromo " + i + " Pred" + fileHandler.SEPARATOR);
                        predictions.add(0, header.deleteCharAt(header.length() - 1).toString());
                        TextWriter.write(predictions, fileHandler.getPredictionsFileName(fold.getName(), run, FileType.CSV));
                    }
                }
            }
            logger.info("Prediction is Done!");
        } catch (CogeeException cex) {
            cex.printStackTrace();
            logger.error(cex.getMessage(), cex);
        }
    }
}
