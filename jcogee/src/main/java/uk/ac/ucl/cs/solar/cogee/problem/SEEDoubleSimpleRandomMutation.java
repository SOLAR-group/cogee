package uk.ac.ucl.cs.solar.cogee.problem;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

@SuppressWarnings("serial")

public class SEEDoubleSimpleRandomMutation implements MutationOperator<DoubleSolution>{
        private double mutationProbability ;
        private RandomGenerator<Double> randomGenerator ;

        public SEEDoubleSimpleRandomMutation(double probability) {
            this(probability, () -> JMetalRandom.getInstance().nextDouble());
        }

        public SEEDoubleSimpleRandomMutation(double probability, RandomGenerator<Double> randomGenerator) {
            if (probability < 0) {
                throw new JMetalException("Mutation probability is negative: " + mutationProbability) ;
            }

            this.mutationProbability = probability ;
            this.randomGenerator = randomGenerator ;
        }

        public double getMutationProbability() {
            return mutationProbability;
        }

        public void setMutationProbability(double mutationProbability) {
            this.mutationProbability = mutationProbability;
        }

        @Override
        public DoubleSolution execute(DoubleSolution solution) throws JMetalException {
            if (null == solution) {
                throw new JMetalException("Null parameter") ;
            }
            doMutation(mutationProbability, solution) ;
            return solution;
        }

        private void doMutation(double probability, DoubleSolution solution) {
            for (int i = 0; i < solution.getNumberOfVariables(); i++) {
                if (randomGenerator.getRandomValue() <= probability) {
                    Double value = solution.getLowerBound(i) +
                            ((solution.getUpperBound(i) - solution.getLowerBound(i)) * randomGenerator.getRandomValue()) ;
                    solution.setVariableValue(i, value) ;
                }
            }
        }
    }


