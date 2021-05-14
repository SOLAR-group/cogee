package uk.ac.ucl.cs.solar.cogee.dataset;

import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;

import java.util.HashMap;
import java.util.Map;

public class EffortEstimationFold extends Fold<Double> {

    public EffortEstimationFold(String name) {
        super(name);
        efforts = new HashMap<>();
    }
    private Map<Integer, Double> efforts;

    public void addEffort(int id, Double effort){
        this.efforts.put(id, effort);
    }

    public Map<Integer, Double> getEfforts(){
        return efforts;
    }

    public Double getEffort(int id) throws CogeeException {
        if(!efforts.containsKey(id))
            throw new CogeeException("There is no such ID!");
        return efforts.get(id);
    }

    public void addFold(EffortEstimationFold fold) throws CogeeException {
        for (DataRow<Double> row : fold.getRows()) {
            this.addRow(row);
            this.efforts.put(row.getId(), fold.getEffort(row.getId()));
        }
    }

    @Override
    public String toString(){
        StringBuilder desc = new StringBuilder();
        desc.append("\nName: " + this.getName());
        desc.append("\nNumber of instances:" + getNumberOfRows());
        return desc.toString();
    }
}
