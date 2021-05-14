package uk.ac.ucl.cs.solar.cogee;

import uk.ac.ucl.cs.solar.cogee.objective.ObjectiveFunctionFactory;

import java.util.List;

public class GAConfigObject {

    private String experiment;
    private String algorithm;
    private List<ObjectiveFunctionFactory.ObjectiveFunction> objectives;
    private int maxGenerations;
    private int maxPopulationSize;
    private double crossoverProbability;
    private double mutationProbability;
    private int maxNumberOfTournaments = 10;
    private int numberOfRuns;

    public GAConfigObject() {
    }

    public GAConfigObject(int maxGenerations,
                          int maxPopulationSize,
                          double crossoverProbability,
                          double mutationProbability,
                          int maxNumberOfTournaments,
                          int numberOfRuns,
                          String experiment,
                          List<ObjectiveFunctionFactory.ObjectiveFunction> objectives
    ) {
        this.maxGenerations = maxGenerations;
        this.maxPopulationSize = maxPopulationSize;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.maxNumberOfTournaments = maxNumberOfTournaments;
        this.numberOfRuns = numberOfRuns;
        this.experiment = experiment;
        this.objectives = objectives;
    }

    public String getMiniConfig() {
        StringBuilder runCnfg = new StringBuilder();
        runCnfg.append(algorithm);
        runCnfg.append("(" + getObjectivesString() + ")");
        runCnfg.append("[G" + maxGenerations);
        runCnfg.append("-P" + maxPopulationSize);
        runCnfg.append("-C" + crossoverProbability);
        runCnfg.append("-M" + mutationProbability);
        runCnfg.append("-R" + numberOfRuns + "]");
        return runCnfg.toString();
    }

    public String getFullConfig() {
        StringBuilder runConfig = new StringBuilder("CoGEE("+ algorithm +") Run For Experiment (" + experiment +
                ") with:\n");
        runConfig.append("    Max Generations: " + maxGenerations + "\n");
        runConfig.append("    Population Size: " + maxPopulationSize + "\n");
        runConfig.append("    Crossover Probability: " + crossoverProbability + "\n");
        runConfig.append("    Mutation Probability: " + mutationProbability + "\n");
        runConfig.append("    Number Of Runs: " + numberOfRuns + "\n");
        return runConfig.toString();
    }

    public void setConfig(String shortConfig) {
        if (shortConfig != null && shortConfig.length() > 0) {
            shortConfig.replace("[", "");
            shortConfig.replace("]", "");
            String[] detail = shortConfig.split("-");
            for (String s : detail) {
                switch (s.trim().charAt(0)) {
                    case 'G':
                        this.maxGenerations = Integer.valueOf(s.substring(1));
                        break;
                    case 'P':
                        this.maxPopulationSize = Integer.valueOf(s.substring(1));
                        break;
                    case 'C':
                        this.crossoverProbability = Double.valueOf(s.substring(1));
                        break;
                    case 'M':
                        this.mutationProbability = Double.valueOf(s.substring(1));
                        break;
                    case 'R':
                        this.numberOfRuns = Integer.valueOf(s.substring(1));
                        break;
                }
            }
        }
    }


    public int getMaxGenerations() {
        return maxGenerations;
    }

    public int getMaxPopulationSize() {
        return maxPopulationSize;
    }

    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    public double getMutationProbability() {
        return mutationProbability;
    }

    public int getMaxNumberOfTournaments() {
        return maxNumberOfTournaments;
    }

    public int getNumberOfRuns() {
        return numberOfRuns;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getExperiment() {
        return experiment;
    }

    public void setExperimentTitle(String experimentTitle) {
        this.experiment = experimentTitle;
    }

    public List<ObjectiveFunctionFactory.ObjectiveFunction> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<ObjectiveFunctionFactory.ObjectiveFunction> objectives) {
        this.objectives = objectives;
    }

    public String getObjectivesString() {
        StringBuilder str = new StringBuilder();
        for (ObjectiveFunctionFactory.ObjectiveFunction objective : objectives) {
            str.append(objective.name() + ",");
        }
        if(str.length() > 0)
            str.deleteCharAt(str.length()-1);
        return str.toString();
    }


}
