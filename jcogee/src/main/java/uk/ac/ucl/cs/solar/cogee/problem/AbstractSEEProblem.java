package uk.ac.ucl.cs.solar.cogee.problem;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import uk.ac.ucl.cs.solar.cogee.dataset.DataRow;
import uk.ac.ucl.cs.solar.cogee.dataset.EffortEstimationFold;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.objective.ObjectiveFunctionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSEEProblem extends AbstractDoubleProblem {


    protected List<ObjectiveFunctionFactory.ObjectiveFunction> OBJECTIVES;



    public AbstractSEEProblem(List<ObjectiveFunctionFactory.ObjectiveFunction> objectives) {
        this.OBJECTIVES = objectives;
        setNumberOfObjectives(OBJECTIVES.size());
    }

    protected EffortEstimationFold trainSet;

    @Override
    public void evaluate(DoubleSolution solution) {
        //get floor for variables in solution
        for (int i = 0; i < solution.getNumberOfVariables(); i++)
            solution.setVariableValue(i, Math.floor(solution.getVariableValue(i)));

        //this map will retain predicted effort
        Map<Integer, Double> prediction = new HashMap<>(trainSet.getNumberOfRows());

        //evaluate solution
        for (DataRow<Double> row : trainSet.getRows()) {
            double predictionValue = predict(solution, row);
            //penalize this solution if it produces negative effort even for a single project!
            if (predictionValue < 0) {
                for (int objective = 0; objective < solution.getNumberOfObjectives(); objective++) {
                    //penalize this solution
                    solution.setObjective(objective, Double.POSITIVE_INFINITY);
                }
                return;
            }
            prediction.put(row.getId(), predictionValue);
        }
        //compute the objective values
        try {
            for (int i = 0; i < OBJECTIVES.size(); i++) {
                solution.setObjective(i,
                        ObjectiveFunctionFactory.getObjectiveFunction(OBJECTIVES.get(i)).compute(trainSet.getEfforts(), prediction)
                );
            }
        } catch (CogeeException e) {
            e.printStackTrace();
        }
        return;
    }

    public Double predict(DoubleSolution chromosome, DataRow<Double> project){
        double predictionValue = 0;
        //try to compute effort for this single project in hand
        for (int i = 0; i < project.getLength(); i++) {
            double w = chromosome.getVariableValue(i * 2);
            double f = project.getValue(i);
            double wf = eval(w, f, chromosome.getVariableValue(i * 2 + 1));
            //add predicted value to
            predictionValue += wf;
        }
        //applying bias weight:
        predictionValue += chromosome.getVariableValue(getNumberOfVariables() - 1);
        //evaluation for a single projects effort finishes here!

        return Math.floor(predictionValue);
    }


    protected double eval(double value1, double value2, Double operator) {
        double result;
        switch (operator.intValue()) {
            case 0:
                result = value1 + value2;
                break;
            case 1:
                result = value1 - value2;
                break;
            case 2:
            default:
                result = value1 * value2;
                break;
            case 3:
                result = value1 / value2;
                break;
        }
        return result;
    }


    public List<ObjectiveFunctionFactory.ObjectiveFunction> getObjectives() {
        return OBJECTIVES;
    }

}
