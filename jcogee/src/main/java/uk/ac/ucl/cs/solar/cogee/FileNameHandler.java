package uk.ac.ucl.cs.solar.cogee;

import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.ioservice.FileType;
import uk.ac.ucl.cs.solar.cogee.ioservice.TextReader;
import uk.ac.ucl.cs.solar.cogee.objective.ObjectiveFunctionFactory;

import java.io.File;
import java.util.*;

public class FileNameHandler {

    private String FILE_NAME;
    private String INPUT_PATH;
    private String OUTPUT_PATH;

    private String GA_CONFIG;
    private static String ROOT_PATH;
    public static final String SEPARATOR = ",";

    private static boolean MAKE_FILE_MODE = true;


    public FileNameHandler(GAConfigObject gaConfig, String dataSetName) {
        this.FILE_NAME = dataSetName;
        if (gaConfig != null) {
            this.GA_CONFIG = gaConfig.getMiniConfig();
            this.OUTPUT_PATH = ROOT_PATH + "output/";
        } else {
            this.GA_CONFIG = "/";
            this.OUTPUT_PATH = ROOT_PATH + "output/";
        }
        this.INPUT_PATH = ROOT_PATH + "dataset/";
        File f = new File(OUTPUT_PATH);
        if(MAKE_FILE_MODE)
            f.mkdir();
    }

    public FileNameHandler(String dataSetName) {
        this.FILE_NAME = dataSetName;
        this.INPUT_PATH = ROOT_PATH + "dataset/";
        OUTPUT_PATH = ROOT_PATH + "output/";
        File f = new File(OUTPUT_PATH);
        if(MAKE_FILE_MODE)
            f.mkdir();
    }

    public static void setRootPath(String rootPath) {
        ROOT_PATH = rootPath;
    }

    public static List<GAConfigObject> readConfigsFile() throws CogeeException {
        List<String> configStr = TextReader.read(FileNameHandler.getCoGEEConfigFileName());
        if (configStr == null || configStr.isEmpty())
            throw new CogeeException("CoGEE Config File is Empty!");
        List<GAConfigObject> configList = new ArrayList<>(configStr.size());
        for (int i = 0; i < configStr.size(); i++) {
            if(configStr.get(i).isEmpty())
                continue;
            String[] line = configStr.get(i).split(":");
            if (line.length != 3)
                throw new CogeeException("There is a problem with reading Config file at line: " + i);
            GAConfigObject conf = new GAConfigObject();
            conf.setAlgorithm(line[0]);
            conf.setObjectives(readObjectives(line[1]));
            conf.setConfig(line[2]);
            configList.add(conf);
        }
        return configList;
    }


    private static List<ObjectiveFunctionFactory.ObjectiveFunction> readObjectives(String objectivesStr){
        List<ObjectiveFunctionFactory.ObjectiveFunction> objectives = new ArrayList<>();
        if(objectivesStr == null || objectivesStr.trim().length()==0)
            return objectives;
        objectivesStr.replace("(", "");
        objectivesStr.replace(")", "");
        String[] objectives_str = objectivesStr.split(",");
        if (objectives_str == null || objectives_str.length == 0)
            return objectives;
        for (int i = 0; i < objectives_str.length; i++) {
            objectives.add(ObjectiveFunctionFactory.ObjectiveFunction.valueOf(objectives_str[i]));
        }
        return objectives;
    }


    public static String getCoGEEConfigFileName() {
        return ROOT_PATH + "cogee" + FileType.CONFIG.getExtention();
    }


    public String getCoGEERootPath() {
        return OUTPUT_PATH + "/" + FILE_NAME + "-" + GA_CONFIG + "/";
    }

    public String getTestFileName() {
        return INPUT_PATH + "/" + FILE_NAME + "-test" + ".xls";
    }

    public String getTrainFileName() {
        return INPUT_PATH + "/" + FILE_NAME + "-train" + ".xls";
    }


    public String getFrontFileName(int run, String fold) {
        File f = new File(getCoGEERootPath());
        if (!f.exists() && MAKE_FILE_MODE)
            f.mkdirs();
        return getCoGEERootPath()
                + FILE_NAME + "-R" + run + "-" + fold + "-GA-PF" + FileType.CSV.getExtention();
    }


    public String getSolutionsFileName(int run, String fold) {
        File f = new File(getCoGEERootPath());
        if (!f.exists() && MAKE_FILE_MODE)
            f.mkdirs();
        return getCoGEERootPath()
                + FILE_NAME + "-R" + run + "-" + fold + "-GA-VAR" + FileType.CSV.getExtention();
    }

    public String getResultFileName( int run, FileType type) {
        File f = new File(getCoGEERootPath());
        if (!f.exists() && MAKE_FILE_MODE)
            f.mkdirs();
        return getCoGEERootPath()
                + FILE_NAME + "-EvaluationResult-PerRun-" + run + "Runs" + type.getExtention();
    }

    public String getPredictionsFileName(String fold, int run, FileType type) {
        File f = new File(getCoGEERootPath());
        if (!f.exists() && MAKE_FILE_MODE)
            f.mkdirs();
        return getCoGEERootPath()
                + FILE_NAME + "-R" + run + "-" + fold + "-Predictions" + type.getExtention();
    }

    public String getPerProjectResultFileName(int run, FileType type) {
        File f = new File(getCoGEERootPath());
        if (!f.exists() && MAKE_FILE_MODE)
            f.mkdirs();
        return getCoGEERootPath()
                + FILE_NAME + "-EvaluationResult-PerProject-" + run + "Runs" +type.getExtention();
    }

    public static void makeFileModeON(){
        MAKE_FILE_MODE = true;
    }
    public static void makeFileModeOFF(){
        MAKE_FILE_MODE = false;
    }

    public boolean makeFileMode(){
        return MAKE_FILE_MODE;
    }

    public static FileType getFileType(String name) {
        FileType type;
        String fileType = name.substring(name.lastIndexOf('.') + 1, name.length());
        switch (fileType) {
            case "xls":
            case "xlsx":
                type = FileType.EXCEL;
                break;
            case "csv":
                type = FileType.CSV;
                break;
            case "txt":
            default:
                type = FileType.TEXT;
                break;
        }
        return type;
    }

}
