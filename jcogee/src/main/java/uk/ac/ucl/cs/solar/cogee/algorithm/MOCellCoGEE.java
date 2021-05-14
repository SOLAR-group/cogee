package uk.ac.ucl.cs.solar.cogee.algorithm;


import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.impl.C9;
import uk.ac.ucl.cs.solar.cogee.GAConfigObject;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.problem.SEEDoubleSimpleRandomMutation;
import uk.ac.ucl.cs.solar.cogee.problem.AbstractSEEProblem;
import uk.ac.ucl.cs.solar.cogee.problem.SEESinglePointCrossover;

public class MOCellCoGEE extends MOCell<DoubleSolution> {

    private GAConfigObject conf;

    public MOCellCoGEE(AbstractSEEProblem problem, GAConfigObject conf) throws CogeeException {
        super(
                problem,
                conf.getMaxPopulationSize() * conf.getMaxGenerations(),
                conf.getMaxPopulationSize(),
                new CrowdingDistanceArchive<>(conf.getMaxPopulationSize()),
                new C9<>((int)Math.sqrt(conf.getMaxPopulationSize()), (int)Math.sqrt(conf.getMaxPopulationSize())),
                new SEESinglePointCrossover(conf.getCrossoverProbability()),
                new SEEDoubleSimpleRandomMutation(conf.getMutationProbability()),
                new BinaryTournamentSelection<>(),
                new SequentialSolutionListEvaluator<>()
        );
        this.conf = conf;
    }


    @Override
    public String toString() {
        
        return conf.toString() +
                "\nThe Constructor: \n" +
                "super(\n" +
                "                problem,\n" +
                "                conf.getMaxPopulationSize() * conf.getMaxGenerations(),\n" +
                "                conf.getMaxPopulationSize(),\n" +
                "                new CrowdingDistanceArchive<>(conf.getMaxPopulationSize()),\n" +
                "                new C9<>((int)Math.sqrt(conf.getMaxPopulationSize()), (int)Math.sqrt(conf.getMaxPopulationSize())),\n" +
                "                new SEESinglePointCrossover(conf.getCrossoverProbability()),\n" +
                "                new SEEDoubleSimpleRandomMutation(conf.getMutationProbability()),\n" +
                "                new BinaryTournamentSelection<>(),\n" +
                "                new SequentialSolutionListEvaluator<>()\n" +
                "        );";
    }
}
