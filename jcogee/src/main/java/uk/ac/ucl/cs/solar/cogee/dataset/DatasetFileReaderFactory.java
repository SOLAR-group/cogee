package uk.ac.ucl.cs.solar.cogee.dataset;

import uk.ac.ucl.cs.solar.cogee.ioservice.DatasetFileReader;
import uk.ac.ucl.cs.solar.cogee.ioservice.ExcelReader;
import uk.ac.ucl.cs.solar.cogee.ioservice.FileType;

public class DatasetFileReaderFactory {

    public static DatasetFileReader getReader(FileType type){
        switch(type){
            case EXCEL:
                return new ExcelReader();
                //TODO NEW READERS CODE HERE
        }
        return null;
    }
}
