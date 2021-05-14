package uk.ac.ucl.cs.solar.cogee.problem;

import uk.ac.ucl.cs.solar.cogee.dataset.EffortEstimationFold;
import uk.ac.ucl.cs.solar.cogee.objective.ObjectiveFunctionFactory;

import java.util.ArrayList;
import java.util.List;

public class SEEProblem extends AbstractSEEProblem {

    final static double WEIGHT_LOWER_LIMIT = -100;
    final static double WEIGHT_UPPER_LIMIT = 100;
    final static double OPERATOR_LOWER_LIMIT = 0.0;
    final static double OPERATOR_UPPER_LIMIT = 3.0;

    public SEEProblem(List<ObjectiveFunctionFactory.ObjectiveFunction> objectives) {
        super(objectives);
    }

    public SEEProblem(List<ObjectiveFunctionFactory.ObjectiveFunction> objectives, EffortEstimationFold trainSetFold) {
        super(objectives);
        setName("SEEProblem");
        this.trainSet = trainSetFold;
        setNumberOfVariables(trainSetFold.getHeader().size() * 2 + 1); //one weight and one operator for each feature in the train set + one bias weight

        List<Double> lowerLimits = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimits = new ArrayList<>(getNumberOfVariables());
        for (int i = 0; i < getNumberOfVariables(); i++) {
            if (i % 2 == 0) {
                lowerLimits.add(WEIGHT_LOWER_LIMIT);
                upperLimits.add(WEIGHT_UPPER_LIMIT);
            } else {
                lowerLimits.add(OPERATOR_LOWER_LIMIT);
                upperLimits.add(OPERATOR_UPPER_LIMIT);
            }
        }
        setLowerLimit(lowerLimits);
        setUpperLimit(upperLimits);
    }

}
