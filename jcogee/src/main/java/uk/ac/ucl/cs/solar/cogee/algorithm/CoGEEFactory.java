package uk.ac.ucl.cs.solar.cogee.algorithm;

import org.apache.log4j.Logger;
import org.uma.jmetal.solution.DoubleSolution;
import uk.ac.ucl.cs.solar.cogee.GAConfigObject;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.problem.AbstractSEEProblem;

import java.util.ArrayList;
import java.util.List;

public class CoGEEFactory {

    static Logger logger = Logger.getLogger(CoGEEFactory.class);

    private IBEACoGEE ibeaCoGEE;
    private MOCellCoGEE moCellCoGEE;
    private NSGA2CoGEE nsga2CoGEE;
    private NSGA3CoGEE nsga3CoGEE;
    private SPEA2CoGEE spea2CoGEE;
    private GA ga;


    private String algorithm;

    public CoGEEFactory() {
    }

    public CoGEEFactory prepareInstance(AbstractSEEProblem problem, GAConfigObject config) throws CogeeException {
        this.algorithm = config.getAlgorithm().trim().toLowerCase();
        switch (this.algorithm) {
            default:
            case "nsga2":
                nsga2CoGEE = new NSGA2CoGEE(problem, config);
                logger.info("NSGAII is build to run!");
                break;
            case "nsga3":
                nsga3CoGEE = new NSGA3CoGEE(problem, config);
                logger.info("NSGAIII is build to run!");
                break;
            case "ibea":
                ibeaCoGEE = new IBEACoGEE(problem, config);
                logger.info("IBEA is build to run!");
                break;
            case "mocell":
                moCellCoGEE = new MOCellCoGEE(problem, config);
                logger.info("MOCell is build to run!");
                break;
            case "spea2":
                spea2CoGEE = new SPEA2CoGEE(problem, config);
                logger.info("SPEA2 is build to run!");
                break;
            case "ga":
                ga = new GA(problem, config);
                logger.info("single objective GA is build to run!");
                break;
        }
        return this;
    }

    public void run(){
        switch (this.algorithm){
            case "nsga2":
                logger.info("Starting to run NSGAII!");
                nsga2CoGEE.run();
                break;
            case "nsga3":
                logger.info("Starting to run NSGAIII!");
                nsga3CoGEE.run();
                break;
            case "ibea":
                logger.info("Starting to run IBEA!");
                ibeaCoGEE.run();
                break;
            case "mocell":
                logger.info("Starting to run MOCell!");
                moCellCoGEE.run();
                break;
            case "spea2":
                logger.info("Starting to run SPEA2!");
                spea2CoGEE.run();
                break;
            case "ga":
                logger.info("Starting to run GA!");
                ga.run();
                break;
        }
    }

    public List<DoubleSolution> getResult(){
        switch (this.algorithm){
            case "nsga2":
                return nsga2CoGEE.getResult();
            case "nsga3":
                return nsga3CoGEE.getResult();
            case "ibea":
                return ibeaCoGEE.getResult();
            case "mocell":
                return moCellCoGEE.getResult();
            case "spea2":
                return spea2CoGEE.getResult();
            case "ga":
                return ga.getFinalPopulation();
//                List<DoubleSolution> result = new ArrayList<>();
//                result.add(ga.getResult());
//                return result;
        }
        return null;
    }
}
