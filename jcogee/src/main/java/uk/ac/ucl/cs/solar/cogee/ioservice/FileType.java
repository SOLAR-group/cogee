package uk.ac.ucl.cs.solar.cogee.ioservice;

public enum FileType {

    EXCEL(".xlsx"),
    CSV(".csv"),
    TEXT(".txt"),
    CONFIG(".cfg");

    private String extention;

    public String getExtention(){
        return extention;
    }

    FileType(String extention){
        this.extention  = extention;
    }
}
