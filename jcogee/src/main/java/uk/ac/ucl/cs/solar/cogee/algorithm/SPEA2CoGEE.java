package uk.ac.ucl.cs.solar.cogee.algorithm;


import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;
import uk.ac.ucl.cs.solar.cogee.GAConfigObject;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.problem.AbstractSEEProblem;
import uk.ac.ucl.cs.solar.cogee.problem.SEEDoubleSimpleRandomMutation;
import uk.ac.ucl.cs.solar.cogee.problem.SEESinglePointCrossover;

import java.util.ArrayList;
import java.util.List;

public class SPEA2CoGEE extends SPEA2<DoubleSolution> {

    private GAConfigObject conf;


    public SPEA2CoGEE(AbstractSEEProblem problem, GAConfigObject conf) throws CogeeException {
        super(
                problem,
                conf.getMaxGenerations() * conf.getMaxPopulationSize(),
                conf.getMaxPopulationSize(),
                new SEESinglePointCrossover(conf.getCrossoverProbability()),
                new SEEDoubleSimpleRandomMutation(conf.getMutationProbability()),
                new BinaryTournamentSelection<>(),
                new SequentialSolutionListEvaluator()
        );
        this.conf = conf;
//        weightOptions = problem.getNumberOfWeightOptions();

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
    public String toString() {
        return conf.toString()+
                "\nThe Constructor: \n" +
                "super(\n" +
                "                problem,\n" +
                "                maxevaluations: conf.getMaxGenerations() * conf.getMaxPopulationSize(),\n" +
                "                conf.getMaxPopulationSize(),\n" +
                "                new SEESinglePointCrossover(conf.getCrossoverProbability()),\n" +
                "                new SEEDoubleSimpleRandomMutation(conf.getMutationProbability()),\n" +
                "                new BinaryTournamentSelection<>(),\n" +
                "                new SequentialSolutionListEvaluator()\n" +
                "        );";
    }
}
