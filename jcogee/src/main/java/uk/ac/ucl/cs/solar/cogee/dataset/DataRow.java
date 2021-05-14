package uk.ac.ucl.cs.solar.cogee.dataset;

import java.util.ArrayList;
import java.util.List;

public class DataRow<T> {

    public DataRow(int id) {
        this.id = id;
        values = new ArrayList<>();
    }

    private int id;
    private List<T> values;
    private int length;

    public int getId() {
        return id;
    }

    public void addValue(T value){
        this.values.add(value);
        this.length = values.size();
    }

    public T getValue(int i){
        return values.get(i);
    }

    public int getLength() {
        return length;
    }

}
