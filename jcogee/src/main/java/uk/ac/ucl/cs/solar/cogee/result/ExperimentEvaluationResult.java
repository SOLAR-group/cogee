package uk.ac.ucl.cs.solar.cogee.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperimentEvaluationResult {

    private Map<Integer, RunEvaluationResult> evaluationResults;

    public ExperimentEvaluationResult(int numberOfRuns) {
        evaluationResults = new HashMap<>(numberOfRuns);
    }

    public void putRunEvaluationResult(RunEvaluationResult result){
        evaluationResults.put(result.getRunId(), result);
    }

    public int getNumberOfRunResults(){
        return evaluationResults.size();
    }

    public Map<Integer, RunEvaluationResult> getResults(){
        return evaluationResults;
    }

    public List<List<Object>> toTable(){
        List<List<Object>> table = null;
        if(getNumberOfRunResults()>0){
            table = new ArrayList<>(evaluationResults.size());
            for (Integer run : evaluationResults.keySet())
                table.add(evaluationResults.get(run).plainRow());
        }
        return table;
    }

    public List<Object> getHeader() {
        return RunEvaluationResult.getHeader();
    }
}
