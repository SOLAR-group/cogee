package uk.ac.ucl.cs.solar.cogee.problem;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;

import java.util.ArrayList;
import java.util.List;

public class SEESinglePointCrossover<S extends Solution> implements CrossoverOperator<S> {

    private double crossoverProbability;
    private RandomGenerator<Double> crossoverRandomGenerator;
    private BoundedRandomGenerator<Integer> pointRandomGenerator;

    public SEESinglePointCrossover(double crossoverProbability) throws CogeeException{
        this(crossoverProbability, () -> {
            return JMetalRandom.getInstance().nextDouble();
        }, (a, b) -> {
            return JMetalRandom.getInstance().nextInt(a, b);
        });
    }

    public SEESinglePointCrossover(double crossoverProbability, RandomGenerator<Double> crossoverRandomGenerator, BoundedRandomGenerator<Integer> pointRandomGenerator) throws CogeeException{
        if (crossoverProbability < 0.0D) {
            throw new CogeeException("Crossover probability is negative: " + crossoverProbability);
        } else {
            this.crossoverProbability = crossoverProbability;
            this.crossoverRandomGenerator = crossoverRandomGenerator;
            this.pointRandomGenerator = pointRandomGenerator;
        }
    }


    public List<S> execute(List<S> solutions) {
        if (solutions == null) {
            throw new JMetalException("Null parameter");
        } else if (solutions.size() != 2) {
            throw new JMetalException("There must be two parents instead of " + solutions.size());
        } else {
            return this.doCrossover(this.crossoverProbability, solutions.get(0), solutions.get(1));
        }
    }

    public List<S> doCrossover(double probability, S father, S mother) {
        List<S> offspring = new ArrayList(2);
        offspring.add((S)father.copy());
        offspring.add((S)mother.copy());
        if (this.crossoverRandomGenerator.getRandomValue() < probability) {
            int numberOfVariables = father.getNumberOfVariables();
            int crossoverPoint = this.pointRandomGenerator.getRandomValue(0, numberOfVariables - 1);

            for (int i = 0; i < crossoverPoint; i++) {
                offspring.get(0).setVariableValue(i, mother.getVariableValue(i));
                offspring.get(1).setVariableValue(i, father.getVariableValue(i));
            }

            for (int i = crossoverPoint; i < numberOfVariables; i++) {
                offspring.get(0).setVariableValue(i, father.getVariableValue(i));
                offspring.get(1).setVariableValue(i, mother.getVariableValue(i));
            }
        }
        return offspring;
    }


    @Override
    public int getNumberOfRequiredParents() {
        return 2;
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 2;
    }

}
