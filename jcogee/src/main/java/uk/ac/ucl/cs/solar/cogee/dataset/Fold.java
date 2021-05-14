package uk.ac.ucl.cs.solar.cogee.dataset;

import java.util.*;

public class Fold<T> {

    public Fold(String name) {
        this.name = name.toLowerCase().replace(" ", "");
        header = new ArrayList<>();
        rows = new ArrayList<>();
    }

    private String name;
    private List<String> header;
    private List<DataRow<T>> rows;
    private int numberOfRows;

    public String getName() {
        return name;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public List<DataRow<T>> getRows() {
        return rows;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void addRow(DataRow<T> row) {
        rows.add(row);
        numberOfRows = rows.size();
    }

    public DataRow<T> getRow(int index){
        return getRow(index);
    }

}
