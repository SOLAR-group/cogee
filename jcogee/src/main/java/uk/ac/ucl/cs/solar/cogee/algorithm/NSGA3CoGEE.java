package uk.ac.ucl.cs.solar.cogee.algorithm;


import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.util.ReferencePoint;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import uk.ac.ucl.cs.solar.cogee.GAConfigObject;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.problem.AbstractSEEProblem;
import uk.ac.ucl.cs.solar.cogee.problem.SEEDoubleSimpleRandomMutation;
import uk.ac.ucl.cs.solar.cogee.problem.SEESelection;
import uk.ac.ucl.cs.solar.cogee.problem.SEESinglePointCrossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class NSGA3CoGEE extends NSGAIII<DoubleSolution> {

    private GAConfigObject conf;


    public NSGA3CoGEE(AbstractSEEProblem problem, GAConfigObject conf) throws CogeeException{
        super(build(problem, conf));
        this.conf = conf;

        numberOfDivisions = new Vector<>(1) ;
        numberOfDivisions.add((conf.getMaxPopulationSize()*2)/problem.getNumberOfObjectives()) ;

        (new ReferencePoint<DoubleSolution>()).generateReferencePoints(referencePoints,getProblem().getNumberOfObjectives() , numberOfDivisions);

        int populationSize = referencePoints.size();
        System.out.println(referencePoints.size());
        while (populationSize%4>0) {
            populationSize++;
        }

        setMaxPopulationSize(populationSize);

        JMetalLogger.logger.info("rpssize: " + referencePoints.size());
    }

    private static NSGAIIIBuilder<DoubleSolution> build(AbstractSEEProblem problem, GAConfigObject conf) throws CogeeException{
        NSGAIIIBuilder<DoubleSolution> builder = new NSGAIIIBuilder<>(problem);
        builder.setPopulationSize(conf.getMaxPopulationSize());
        builder.setMaxIterations(conf.getMaxGenerations());
        builder.setCrossoverOperator(new SEESinglePointCrossover(conf.getCrossoverProbability()));
        builder.setMutationOperator(new SEEDoubleSimpleRandomMutation(conf.getMutationProbability()));
        builder.setSelectionOperator(new SEESelection(conf.getMaxNumberOfTournaments()));
        builder.setSolutionListEvaluator(new SequentialSolutionListEvaluator());
        return builder;
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
        List<DoubleSolution> offspringPopulation = new ArrayList<>(population.size());
        for (int i = 0; i < population.size(); i+=2) {
            List<DoubleSolution> parents = new ArrayList<>(2);
            parents.add(population.get(i));
            parents.add(population.get(Math.min(i + 1, getMaxPopulationSize()-1)));

            List<DoubleSolution> offspring = crossoverOperator.execute(parents);

            mutationOperator.execute(offspring.get(0));
            mutationOperator.execute(offspring.get(1));

            offspringPopulation.add(offspring.get(0));
            offspringPopulation.add(offspring.get(1));
        }
        return offspringPopulation ;
    }


    protected List<DoubleSolution> selection(List<DoubleSolution> population, int matingPoolSize) {
        List<DoubleSolution> matingPopulation = (List<DoubleSolution>) ((SEESelection)this.selectionOperator).execute(population, matingPoolSize);
        return matingPopulation;
    }


    @Override
    public void run() {
        List<DoubleSolution> offspringPopulation;
        List<DoubleSolution> matingPopulation;

        population = createInitialPopulation();
        population = evaluatePopulation(population);
        initProgress();
        while (!isStoppingConditionReached()) {
            matingPopulation = selection(population, population.size());
            offspringPopulation = reproduction(matingPopulation);
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
