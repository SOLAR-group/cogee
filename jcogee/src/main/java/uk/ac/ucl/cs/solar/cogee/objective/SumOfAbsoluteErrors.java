package uk.ac.ucl.cs.solar.cogee.objective;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SumOfAbsoluteErrors extends AbstractSEEObjectiveFunction implements SEEObjectiveFunction {

    private static SumOfAbsoluteErrors sae;

    private SumOfAbsoluteErrors(){
        codeName = "SAE";
    }

    public static SumOfAbsoluteErrors getInstance(){
        if(sae == null)
            sae = new SumOfAbsoluteErrors();
        return sae;
    }

    @Override
    public double compute(Map<Integer, Double> effort, Map<Integer, Double> prediction) {
        List<Double> residuals = effort.keySet().stream().map(id -> Math.abs(effort.get(id) - prediction.get(id))).collect(Collectors.toList());
        return compute(residuals);
    }

    @Override
    public double compute(List<Double> absoluteResiduals){
        double sum = 0.0;
        for(double d: absoluteResiduals)
            sum += d;
        return Math.floor(sum);
    }
}
