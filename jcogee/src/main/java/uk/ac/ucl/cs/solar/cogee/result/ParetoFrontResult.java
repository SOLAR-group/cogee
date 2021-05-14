package uk.ac.ucl.cs.solar.cogee.result;

import org.uma.jmetal.solution.DoubleSolution;
import uk.ac.ucl.cs.solar.cogee.FileNameHandler;

import java.util.ArrayList;
import java.util.List;

public class ParetoFrontResult {

    private Integer runId;
    private Integer projectId;
    //the list size is equal to the Pareto front size
    private Double actualEffort;
    private List<DoubleSolution> front;
    private List<Double> predictions;
    private List<String> predictions_str;
    private List<Double> residuals;
    private Double meanAE;
    private Double medianAE;

    public ParetoFrontResult(Integer projectId, Double actualEffort) {
        this.projectId = projectId;
        this.actualEffort = actualEffort;
        predictions = new ArrayList<>();
        predictions_str = new ArrayList<>();
        residuals = new ArrayList<>();
    }

    public Integer getRunId() {
        return runId;
    }

    public void setRunId(Integer runId) {
        this.runId = runId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public List<DoubleSolution> getFront() {
        return front;
    }

    public void setFront(List<DoubleSolution> front) {
        this.front = front;
    }

    public List<Double> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Double> predictions) {
        this.predictions = predictions;
    }

    public List<Double> getResiduals() {
        return residuals;
    }

    public void setResiduals(List<Double> residuals) {
        this.residuals = residuals;
    }

    public Double getMeanAE() {
        return meanAE;
    }

    public void setMeanAE(Double meanAE) {
        this.meanAE = meanAE;
    }

    public Double getMedianAE() {
        return medianAE;
    }

    public void setMedianAE(Double medianAE) {
        this.medianAE = medianAE;
    }

    public Double getActualEffort() {
        return actualEffort;
    }

    public void setActualEffort(Double actualEffort) {
        this.actualEffort = actualEffort;
    }

    public List<String> getPredictions_str() {
        return predictions_str;
    }

    public void setPredictions_str(List<String> predictions_str) {
        this.predictions_str = predictions_str;
    }

    public String printPredictions(){
        if(predictions_str == null || predictions_str.isEmpty())
            return "";
        String separator = FileNameHandler.SEPARATOR;
        StringBuilder str = new StringBuilder();
        for (String prediction : predictions_str) {
            str.append(prediction + separator);
        }
        return str.deleteCharAt(str.length()-1).toString();
    }

}