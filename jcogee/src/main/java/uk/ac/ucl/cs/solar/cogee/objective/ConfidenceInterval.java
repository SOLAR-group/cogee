package uk.ac.ucl.cs.solar.cogee.objective;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfidenceInterval extends AbstractSEEObjectiveFunction implements SEEObjectiveFunction {

    private static ConfidenceInterval CI;

    private ConfidenceInterval(){
        codeName = "CI";
    }

    public static ConfidenceInterval getInstance(){
        if(CI == null)
            CI = new ConfidenceInterval();
        return CI;
    }

    @Override
    public double compute(Map<Integer, Double> effort, Map<Integer, Double> prediction) {
        List<Double> residuals = effort.keySet().stream().map(id -> Math.abs(effort.get(id) - prediction.get(id))).collect(Collectors.toList());
        return compute(residuals);
    }

    @Override
    public double compute(List<Double> absoluteResiduals){
        if(absoluteResiduals.size()<2)
            return 0;
        DescriptiveStatistics ds = new DescriptiveStatistics();
        absoluteResiduals.stream().forEach(aDouble -> ds.addValue(aDouble));
        double devSt = ds.getStandardDeviation();
        TDistribution tDistribution = new TDistribution(null, absoluteResiduals.size()-1);
        double value = tDistribution.inverseCumulativeProbability(0.95) * devSt / Math.sqrt(absoluteResiduals.size());
        return Math.floor(value);
    }

}
