package uk.ac.ucl.cs.solar.cogee.problem;

import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.TournamentSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SEESelection<S extends Solution<?>> implements SelectionOperator<List<S>, List<S>> {

    private Comparator<S> comparator;
    private final int maxNumberOfTournaments;
    private JMetalRandom random;
    private TournamentSelection<S> tournamentSelection;

    public SEESelection(int maxNumberOfTournaments) {
        this(new DominanceComparator(), maxNumberOfTournaments);
    }


    public SEESelection(Comparator<S> comparator, int maxNumberOfTournaments) {
        this.maxNumberOfTournaments = maxNumberOfTournaments;
        this.comparator = comparator;
        this.random = JMetalRandom.getInstance();
    }


    public List<S> execute(List<S> population) {
        if (null == population) {
            throw new JMetalException("The solution list is null");
        } else if (population.isEmpty()) {
            throw new JMetalException("The solution list is empty");
        } else if (maxNumberOfTournaments > population.size()) {
            throw new JMetalException("The maximum number of Tournaments id greater than the size of population");
        } else {
            List<S> matingPopulation = new ArrayList(population.size());
            if (population.size() == 1) {
                matingPopulation.add(population.get(0));
            } else {
                matingPopulation.addAll(doRouletteWheel(population, population.size()));
            }

            return matingPopulation;

        }
    }

    public List<S> execute(List<S> population, int poolSize) {
        if (null == population) {
            throw new JMetalException("The solution list is null");
        } else if (population.isEmpty()) {
            throw new JMetalException("The solution list is empty");
        } else if (maxNumberOfTournaments > population.size()) {
            throw new JMetalException("The maximum number of Tournaments id greater than the size of population");
        } else {
            List<S> matingPopulation = new ArrayList(population.size());
            if (population.size() == 1) {
                matingPopulation.add(population.get(0));
            } else {
                matingPopulation.addAll(doRouletteWheel(population, poolSize));
            }

            return matingPopulation;

        }
    }

    public List<S> doTournament(List<S> population, int numberOfTournamentWinners) {
        tournamentSelection = new TournamentSelection<>(2);
        List<S> tournamentWinners = new ArrayList<>(numberOfTournamentWinners);
        for (int i = 0; i < numberOfTournamentWinners; i++) {
            tournamentWinners.add(tournamentSelection.execute(population));
        }
        return tournamentWinners;
    }

    private List<S> doRouletteWheel(List<S> fullPopulation, int numberOfRouletteWheels) {
        List<S> rouletteResults = new ArrayList<>(numberOfRouletteWheels);
        List<S> population = new ArrayList<>();
        for (S s : fullPopulation) {
            if (s.getObjective(0) != Double.POSITIVE_INFINITY && s.getObjective(0) != Double.NEGATIVE_INFINITY)
                population.add(s);
        }
        double[] maxs = new double[population.get(0).getNumberOfObjectives()];
        for (int i = 0; i < population.get(0).getNumberOfObjectives(); i++) {
            maxs[i] = Double.MIN_VALUE;
        }
        for (S solution : population) {
            for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
                if (maxs[i] < solution.getObjective(i))
                    maxs[i] = solution.getObjective(i);
            }
        }
        double[] cumulativeFitnesses = new double[population.size()];
        double[] overallFitness = new double[population.size()];

        for (int j = 0; j < population.get(0).getNumberOfObjectives(); j++)
            overallFitness[0] += population.get(0).getObjective(j) / maxs[j];
        cumulativeFitnesses[0] = overallFitness[0];
        for (int i = 1; i < population.size(); i++) {
            for (int j = 0; j < population.get(i).getNumberOfObjectives(); j++)
                overallFitness[i] += population.get(i).getObjective(j) / maxs[j];
            cumulativeFitnesses[i] = cumulativeFitnesses[i - 1] + overallFitness[i];
        }

        for (int n = 0; n < numberOfRouletteWheels; n++) {
            double randomFitness = random.nextDouble() * cumulativeFitnesses[cumulativeFitnesses.length - 1];
            int index = Arrays.binarySearch(cumulativeFitnesses, randomFitness);
            if (index < 0) {
                // Convert negative insertion point to array index.
                index = Math.abs(index + 1);
            }
            rouletteResults.add(population.get(index));
        }
        return rouletteResults;
    }


}
