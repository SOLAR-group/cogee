package uk.ac.ucl.cs.solar.cogee.algorithm;


import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;
import uk.ac.ucl.cs.solar.cogee.GAConfigObject;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.problem.AbstractSEEProblem;
import uk.ac.ucl.cs.solar.cogee.problem.SEEDoubleSimpleRandomMutation;
import uk.ac.ucl.cs.solar.cogee.problem.SEESelection;
import uk.ac.ucl.cs.solar.cogee.problem.SEESinglePointCrossover;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NSGA2CoGEE extends NSGAII<DoubleSolution> {

    private GAConfigObject conf;

    PseudoRandomGenerator randomGen = JMetalRandom.getInstance().getRandomGenerator();

    public NSGA2CoGEE(AbstractSEEProblem problem, GAConfigObject conf) throws CogeeException {
        super(
                problem,
                conf.getMaxGenerations() * conf.getMaxPopulationSize(),
                conf.getMaxPopulationSize(),
                new SEESinglePointCrossover(conf.getCrossoverProbability()),
                new SEEDoubleSimpleRandomMutation(conf.getMutationProbability()),
                new SEESelection(conf.getMaxNumberOfTournaments()),
                new SequentialSolutionListEvaluator()
        );
        this.conf = conf;

    }

    @Override
    protected List<DoubleSolution> createInitialPopulation() {
        List<DoubleSolution> population = new ArrayList(this.getMaxPopulationSize());
        while (population.size() < this.getMaxPopulationSize()) {
            DoubleSolution newIndividual = this.getProblem().createSolution();
            problem.evaluate(newIndividual);
            if (newIndividual.getObjective(0) != Double.POSITIVE_INFINITY)
                population.add(newIndividual);
        }
        return population;
    }


    @Override
    protected List<DoubleSolution> reproduction(List<DoubleSolution> population) {
        int numberOfParents = this.crossoverOperator.getNumberOfRequiredParents();
        this.checkNumberOfParents(population, numberOfParents);
        List<DoubleSolution> offspringPopulation = new ArrayList(population.size());

        for (int i = 0; i < population.size(); i += numberOfParents) {
            List<DoubleSolution> parents = new ArrayList(numberOfParents);
            for (int j = 0; j < numberOfParents; ++j) {
                parents.add(population.get(i + j));
            }
            List<DoubleSolution> offspring = this.crossoverOperator.execute(parents);
            Iterator var7 = offspring.iterator();
            while (var7.hasNext()) {
                DoubleSolution s = (DoubleSolution) var7.next();
                this.mutationOperator.execute(s);
                offspringPopulation.add(s);
            }
        }
        return offspringPopulation;
    }


    protected List<DoubleSolution> selection(List<DoubleSolution> population, int matingPoolSize) {
        List<DoubleSolution> matingPopulation = (List<DoubleSolution>) ((SEESelection) this.selectionOperator).execute(population, matingPoolSize);
        return matingPopulation;
    }

    @Override
    public void run() {
        List<DoubleSolution> offspringPopulation;
        List<DoubleSolution> matingPopulation;
        List<DoubleSolution> survivors;

        population = createInitialPopulation();
        population = evaluatePopulation(population);
        initProgress();
        while (!isStoppingConditionReached()) {
            int numberOfSurvivors = randomGen.nextInt(1, conf.getMaxNumberOfTournaments());
            if (numberOfSurvivors % 2 == 1)
                numberOfSurvivors--;
            matingPopulation = selection(population, maxPopulationSize - numberOfSurvivors);
            survivors = ((SEESelection) selectionOperator).doTournament(population, numberOfSurvivors);
            offspringPopulation = reproduction(matingPopulation);
            offspringPopulation.addAll(survivors);
            offspringPopulation = evaluatePopulation(offspringPopulation);
            population = replacement(population, offspringPopulation);
            updateProgress();
        }
    }

    @Override
    public String toString() {
        return conf.toString();
    }
}
