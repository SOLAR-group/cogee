package uk.ac.ucl.cs.solar.cogee.result;

import java.util.ArrayList;
import java.util.List;

public class RunEvaluationResult {

    private int runId;

    private Double meanMAE;
    private Double medianMAE;
    private Double meanMdAE;
    private Double medianMdAE;
    private Double CI;

    public RunEvaluationResult(int runId) {
        this.runId = runId;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public Double getMeanMAE() {
        return meanMAE;
    }

    public void setMeanMAE(Double meanMAE) {
        this.meanMAE = meanMAE;
    }

    public Double getMedianMAE() {
        return medianMAE;
    }

    public void setMedianMAE(Double medianMAE) {
        this.medianMAE = medianMAE;
    }

    public Double getMeanMdAE() {
        return meanMdAE;
    }

    public void setMeanMdAE(Double meanMdAE) {
        this.meanMdAE = meanMdAE;
    }

    public Double getMedianMdAE() {
        return medianMdAE;
    }

    public void setMedianMdAE(Double medianMdAE) {
        this.medianMdAE = medianMdAE;
    }

    public Double getCI() {
        return CI;
    }

    public void setCI(Double CI) {
        this.CI = CI;
    }

    public List<Object> plainRow(){
        List<Object> row = new ArrayList<>();
        row.add(Integer.valueOf(getRunId()));
        row.add(getMeanMAE());
        row.add(getMedianMAE());
        row.add(getMeanMdAE());
        row.add(getMedianMdAE());
        row.add(getCI());
        return row;
    }

    public static List<Object> getHeader(){
        List<Object> rowHeader = new ArrayList<>();
        rowHeader.add("Run");
        rowHeader.add("meanMAE");
        rowHeader.add("medianMAE");
        rowHeader.add("meanMdAE");
        rowHeader.add("medianMdAE");
        rowHeader.add("CI");
        return rowHeader;
    }
}
