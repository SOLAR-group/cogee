package uk.ac.ucl.cs.solar.cogee.ioservice;

import org.apache.log4j.Logger;
import uk.ac.ucl.cs.solar.cogee.FileNameHandler;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TextWriter {

    static Logger logger = Logger.getLogger(TextWriter.class);

    public static void write(List<String> text, String fullName) throws CogeeException {
        if (text == null || text.size() == 0)
            logger.info("Writing File [" + fullName + "] was NOT successful because text to be written in the file is empty!");
        if (fullName == null || fullName.isEmpty())
            throw new CogeeException("file name is not provided!");
        try {
            Path resultsFile = Paths.get(fullName);
            Files.write(resultsFile, text, Charset.forName("UTF-8"));
            logger.info("File ["+fullName+"] is saved!");
        } catch (IOException iox) {
            logger.error(iox);
        }
    }

    public static void write(List<List<Object>> table, String fileFullName, List<Object> header) throws CogeeException {
        write(table, fileFullName, header, FileNameHandler.SEPARATOR);
    }

    public static void write(List<List<Object>> table, String fileFullName, List<Object> header, String separator) throws CogeeException{
        if (table == null || table.size() == 0)
            logger.warn("Writing File ["+fileFullName+"] was NOT successful because text to be written in the file is empty!");
        if (fileFullName == null || fileFullName.isEmpty())
            throw new CogeeException("File name is not provided!");
        if(separator==null)
            separator = FileNameHandler.SEPARATOR;
        else
            if(separator.length()>1)
                throw new CogeeException("Separator must be a character. ["+ separator +"] is not acceptable!");
        try {
            Path resultsFile = Paths.get(fileFullName);

            List<String> text = new ArrayList<>();
            if (header != null && header.size() > 0) {
                StringBuilder hdr = new StringBuilder();
                for (Object colHeader : header) {
                    hdr.append(colHeader + separator);
                }
                hdr.deleteCharAt(hdr.length()-1);
                text.add(hdr.toString());
            }
            for (List<Object> doubles : table) {
                String line = "";
                for (Object item : doubles) {
                    line += item.toString() + separator;
                }
                line = line.substring(0, line.length()-1);
                text.add(line);
            }
            Files.write(resultsFile, text, Charset.forName("UTF-8"));
            logger.info("File ["+fileFullName+"] is saved!");
        } catch (IOException iox) {
            logger.error(iox);
        }
    }
}
