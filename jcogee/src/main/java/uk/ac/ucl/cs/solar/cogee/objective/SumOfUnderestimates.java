package uk.ac.ucl.cs.solar.cogee.objective;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SumOfUnderestimates extends AbstractSEEObjectiveFunction implements SEEObjectiveFunction {

    private static SumOfUnderestimates sue;

    private SumOfUnderestimates() {
        codeName = "SUE";
    }

    public static SumOfUnderestimates getInstance() {
        if (sue == null)
            sue = new SumOfUnderestimates();
        return sue;
    }

    @Override
    public double compute(Map<Integer, Double> effort, Map<Integer, Double> prediction) {
        List<Double> residuals = effort.keySet().stream().map(id -> (effort.get(id) - prediction.get(id))).collect(Collectors.toList());
        return compute(residuals);
    }

    @Override
    public double compute(List<Double> residuals) {
        double sum = 0.0;
        for (double d : residuals)
            if (d > 0)
                sum += d;
        return Math.floor(sum);
    }
}
