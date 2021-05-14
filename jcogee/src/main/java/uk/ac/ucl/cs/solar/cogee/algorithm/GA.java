package uk.ac.ucl.cs.solar.cogee.algorithm;


import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import uk.ac.ucl.cs.solar.cogee.GAConfigObject;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.problem.AbstractSEEProblem;
import uk.ac.ucl.cs.solar.cogee.problem.SEEDoubleSimpleRandomMutation;
import uk.ac.ucl.cs.solar.cogee.problem.SEESelection;
import uk.ac.ucl.cs.solar.cogee.problem.SEESinglePointCrossover;

import java.util.*;

public class GA extends GenerationalGeneticAlgorithm<DoubleSolution> {

    private GAConfigObject conf;
    private Comparator<DoubleSolution> comparator;



    public GA(AbstractSEEProblem problem, GAConfigObject conf) throws CogeeException {
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
        if(problem.getObjectives().size()>1)
            throw new CogeeException("More than one objective is set for a single Objective GA!");
        this.comparator  = new ObjectiveComparator<DoubleSolution>(0);
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


    @Override
    protected List<DoubleSolution> replacement(List<DoubleSolution> population, List<DoubleSolution> offspringPopulation) {
        Collections.sort(population, comparator);
        //The R Version of rbga function uses 20% elitism by default. It means 20% best individuals of the population transferred to the next generation.
        int numberOfSurvivors = (int) Math.round(population.size() * 0.2);
        offspringPopulation.addAll(population.subList(0,numberOfSurvivors));
        Collections.sort(offspringPopulation, comparator) ;
        return offspringPopulation.subList(0, maxPopulationSize);
    }

    @Override
    public void run() {
        List<DoubleSolution> offspringPopulation;

        population = createInitialPopulation();
        population = evaluatePopulation(population);
        initProgress();
        while (!isStoppingConditionReached()) {
            offspringPopulation = reproduction(population);
            offspringPopulation = evaluatePopulation(offspringPopulation);
            population = replacement(population, offspringPopulation);
            updateProgress();
        }
    }

    public List<DoubleSolution> getFinalPopulation(){
        Collections.sort(getPopulation(), comparator) ;
        return getPopulation();
    }

    @Override
    public String toString() {
        return conf.toString();
    }
}
