package uk.ac.ucl.cs.solar.cogee.objective;

import java.util.List;
import java.util.Map;

public class MeanAbsoluteError extends AbstractSEEObjectiveFunction implements SEEObjectiveFunction{

    private static MeanAbsoluteError mae;

    private MeanAbsoluteError(){
        codeName = "MAE";
    }

    public static MeanAbsoluteError getInstance(){
        if(mae == null)
            mae = new MeanAbsoluteError();
        return mae;
    }

    @Override
    public double compute(Map<Integer, Double> effort, Map<Integer, Double> prediction) {
        return Math.floor(SumOfAbsoluteErrors.getInstance().compute(effort, prediction)/effort.size());
    }

    @Override
    public double compute(List<Double> residuals) {
        return Math.floor(SumOfAbsoluteErrors.getInstance().compute(residuals)/residuals.size());    }

}
