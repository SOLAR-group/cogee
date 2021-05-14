package uk.ac.ucl.cs.solar.cogee.ioservice;

import uk.ac.ucl.cs.solar.cogee.dataset.Dataset;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;

import java.io.File;

public interface DatasetFileReader {

    Dataset read(File file) throws CogeeException;

}
