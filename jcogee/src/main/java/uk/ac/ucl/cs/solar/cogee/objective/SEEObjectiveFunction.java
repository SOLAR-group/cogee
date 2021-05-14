package uk.ac.ucl.cs.solar.cogee.objective;

import java.util.List;
import java.util.Map;

public interface SEEObjectiveFunction {

    double compute(Map<Integer, Double> measure, Map<Integer, Double> prediction);

    double compute(List<Double> residuals);
}
