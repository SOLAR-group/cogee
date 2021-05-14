package uk.ac.ucl.cs.solar.cogee.ioservice;

import org.apache.log4j.Logger;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TextReader {

    static Logger logger = Logger.getLogger(TextReader.class);

    public static List<String> read(String fullName) throws CogeeException {
        if (fullName == null || fullName.isEmpty())
            throw new CogeeException("file name is not provided!");
        try {
            Path resultsFile = Paths.get(fullName);
            if(!Files.exists(resultsFile))
                throw new CogeeException("File Doesn't Exist: " + fullName);
            List<String> text = Files.readAllLines(resultsFile, Charset.forName("UTF-8"));
            logger.info("File ["+fullName+"] is read and ready!");
            return text;
        } catch (IOException iox) {
            iox.printStackTrace();
            throw new CogeeException("File Doesn't Exist: " + fullName);
        }
    }

}
