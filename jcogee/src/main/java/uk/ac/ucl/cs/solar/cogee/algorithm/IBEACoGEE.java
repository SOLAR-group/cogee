package uk.ac.ucl.cs.solar.cogee.algorithm;


import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.DoubleSolution;
import uk.ac.ucl.cs.solar.cogee.GAConfigObject;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.problem.AbstractSEEProblem;
import uk.ac.ucl.cs.solar.cogee.problem.SEEDoubleSimpleRandomMutation;
import uk.ac.ucl.cs.solar.cogee.problem.SEESinglePointCrossover;

public class IBEACoGEE extends IBEA<DoubleSolution> {

    private GAConfigObject conf;

    public IBEACoGEE(AbstractSEEProblem problem, GAConfigObject conf) throws CogeeException {
        super(
                problem,
                conf.getMaxPopulationSize(),
                conf.getMaxPopulationSize(),
                conf.getMaxGenerations() * conf.getMaxPopulationSize(),
                new BinaryTournamentSelection<>(),
                new SEESinglePointCrossover(conf.getCrossoverProbability()),
                new SEEDoubleSimpleRandomMutation(conf.getMutationProbability())
        );
        this.conf = conf;
    }


    @Override
    public String toString() {
        return conf.toString() +
                "\nThe Constructor: \n" +
                "super(\n" +
                "                problem,\n" +
                "                conf.getMaxPopulationSize(),\n" +
                "                conf.getMaxPopulationSize(),\n" +
                "                maxevaluations: conf.getMaxGenerations() * conf.getMaxPopulationSize(),\n" +
                "                new BinaryTournamentSelection<>(),\n" +
                "                new SEESinglePointCrossover(conf.getCrossoverProbability()),\n" +
                "                new PolynomialMutation(conf.getMutationProbability(), 20)\n" +
                "        );";
    }
}
