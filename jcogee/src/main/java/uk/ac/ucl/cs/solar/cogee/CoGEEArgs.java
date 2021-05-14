package uk.ac.ucl.cs.solar.cogee;

import org.apache.log4j.Logger;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;

public class CoGEEArgs {

    static Logger logger = Logger.getLogger(CoGEEArgs.class);

    private String root = "."; //default value = current path
    private String function = "cogee";   // default value = run CoGEE
    private String dataset;  // default value = maxwell dataset

    public CoGEEArgs(String args[]) throws CogeeException {
        if (args != null && args.length > 0) {
            logger.info("Arguments Provided:");
            int poz = 0;
            while (poz < args.length) {
                if (args[poz].toLowerCase().equals("-func")) {
                    if (args.length > poz) {
                        setFunction(args[++poz]);
                        logger.info("Function: " + printFunction());
                    } else
                        throw new CogeeException("No value is provided for -func argument!");
                    poz++;
                } else if (args[poz].toLowerCase().equals("-ds")) {
                    if (args.length > poz) {
                        setDataset(args[++poz]);
                        logger.info("Dataset: " + getDataset());
                    } else
                        throw new CogeeException("No value is provided for -ds argument!");
                    poz++;
                } else if (args[poz].toLowerCase().equals("-path")) {
                    if (args.length > poz) {
                        setRoot(args[++poz]);
                        logger.info("Path: " + getRoot());
                    } else
                        throw new CogeeException("No value is provided for -path argument!");
                    poz++;
                }
                else {
                    if(args[poz].startsWith("-"))
                        throw new CogeeException("UNRECOGNISABLE PARAMETER! -> "  + args[poz]);
                }
            }
        }

    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        if (!root.isEmpty()) {
            if (!(root.charAt(root.length() - 1) == '/' || root.charAt(root.length() - 1) == '\\'))
                root += "/";
        } else
            root = "./";
        this.root = root;
    }

    public String getFunction() throws CogeeException{
        if(!function.isEmpty())
            return function;
         else
            throw new CogeeException("No Option passed for -func Parameter!");
    }

    public String printFunction() throws CogeeException{
        StringBuilder str = new StringBuilder();
        if(getFunction().length()>0) {
            if (function.contains("cogee"))
                str.append("Run CoGEE");
            if (function.contains("pred")) {
                if (str.length() > 0)
                    str.append(", ");
                str.append("Produce Predictions");
            }
            if (function.contains("eval")) {
                if (str.length() > 0)
                    str.append(" and ");
                str.append("Evaluate");
            }
            if (str.length() > 0)
                str.append("!");
        }
        return str.toString();
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String function) {
        this.dataset = function.trim().toLowerCase();
    }
}
