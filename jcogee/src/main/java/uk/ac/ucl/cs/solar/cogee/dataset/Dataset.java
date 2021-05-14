package uk.ac.ucl.cs.solar.cogee.dataset;

import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;

import java.util.ArrayList;
import java.util.List;

public class Dataset<T extends Fold> {

    public Dataset(String name, int numberOfFolds) {
        this.name = name.toLowerCase();
        folds = new ArrayList<>(numberOfFolds);
    }

    private String name;
    private List<T> folds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public List<T> getFolds() {
        return folds;
    }

    public void addFold(T fold) {
        this.folds.add(fold);
    }

    public T getFoldByName(String name) throws CogeeException {
        for (T fold : folds) {
            if (fold.getName().replace(" ", "").equals(name.toLowerCase().replace(" ", "")))
                return fold;
        }
        throw new CogeeException("No such fold in the dataset by name: " + name);
    }

    @Override
    public String toString(){
        StringBuilder desc = new StringBuilder();
        desc.append("\n");
        desc.append("DataSet name: ");
        desc.append(this.getName());
        desc.append("\nFolds:");
        for (T fold : folds) {
            desc.append(fold.toString());
        }
        return desc.toString();
    }
}
