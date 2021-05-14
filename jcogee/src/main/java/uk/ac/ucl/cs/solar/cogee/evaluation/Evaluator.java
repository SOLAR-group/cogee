package uk.ac.ucl.cs.solar.cogee.evaluation;

import org.uma.jmetal.solution.DoubleSolution;
import uk.ac.ucl.cs.solar.cogee.dataset.DataRow;
import uk.ac.ucl.cs.solar.cogee.dataset.EffortEstimationFold;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.objective.ObjectiveFunctionFactory;
import uk.ac.ucl.cs.solar.cogee.objective.ConfidenceInterval;
import uk.ac.ucl.cs.solar.cogee.problem.SEEProblem;
import uk.ac.ucl.cs.solar.cogee.result.ParetoFrontResult;

import java.util.*;

public class Evaluator {


    //Gets a list of solutions (Pareto front) and a fold (testFold) and computes residuals for each project-solution
    //Adds them to a table (rows are projects specified by their Ids, and columns are solutions specified by the chromosome number)
    //Last two columns are mean and median for their corresponding row
    public static Map<Integer, ParetoFrontResult> getParetoFrontResults(List<ObjectiveFunctionFactory.ObjectiveFunction> objectives, List<DoubleSolution> paretoFront, EffortEstimationFold fold) throws CogeeException {
        if (paretoFront == null || paretoFront.size() == 0)
            return new HashMap<>();
        SEEProblem problem = new SEEProblem(objectives, fold);
        Map<Integer, ParetoFrontResult> result = new HashMap<>();
        for (DataRow<Double> row : fold.getRows()) {
            ParetoFrontResult pfr = new ParetoFrontResult(row.getId(), fold.getEffort(row.getId()));
            List<Double> ael = new ArrayList<>();
            double sum = 0.0;
            for (int i = 0; i < paretoFront.size(); i++) {
                double prediction = problem.predict(paretoFront.get(i), row);
                if (prediction >= 0) {
                    pfr.getPredictions().add(prediction);
                    pfr.getPredictions_str().add(String.valueOf(prediction));
                    double aev = Math.abs(prediction - fold.getEffort(row.getId()));
                    pfr.getResiduals().add(aev);
                    ael.add(aev);
                    sum += aev;
                }else
                    pfr.getPredictions_str().add("NA");
            }
            double median = getMedianForList(ael);
            pfr.setMedianAE(median);
            pfr.setMeanAE(round(sum / ael.size(), 2));
            result.put(pfr.getProjectId(), pfr);
        }
        return result;
    }

    public static Double getCIForList(List<Double> mae) {
        return ConfidenceInterval.getInstance().compute(mae);
    }

    public static double getMedianForList(List<Double> list) {
        double median = 0;
        if (list != null && list.size() > 0) {
            double[] listToSort = list.stream().mapToDouble(Double::doubleValue).toArray();
            Arrays.sort(listToSort);
            if (listToSort.length % 2 == 0) {
                int mid = listToSort.length / 2;
                median = (listToSort[mid - 1] + listToSort[mid]) / 2;
            } else
                median = listToSort[listToSort.length / 2];
        }
        return round(median, 2);
    }

    public static double getSumForList(List<Double> list) {
        double sum = 0;
        if (list != null && list.size() == 0)
            return sum;
        for (Double aDouble : list) {
            sum += aDouble;
        }
        return round(sum, 2);
    }

    public static double getMeanForList(List<Double> list) {
        double sum = getSumForList(list);
        if (sum == 0)
            return 0;
        else
            return round(sum / list.size(), 2);
    }

    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

}
