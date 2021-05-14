package uk.ac.ucl.cs.solar.cogee.ioservice;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import uk.ac.ucl.cs.solar.cogee.FileNameHandler;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.objective.ObjectiveFunctionFactory;
import uk.ac.ucl.cs.solar.cogee.problem.SEEProblem;

import java.util.ArrayList;
import java.util.List;

public class ResultReader {


    public static List<DoubleSolution> readFront(String variableFileName, SEEProblem problem) throws CogeeException {
        List<String> var = TextReader.read(variableFileName);
        List<DoubleSolution> front;
        if (var == null || var.isEmpty())
            throw new CogeeException("Variables file is empty or there was a problem reading it!" +
                    "\n" + variableFileName);
        front = new ArrayList<>(var.size());
        for (String chromosome : var) {
            if (chromosome.trim().endsWith(FileNameHandler.SEPARATOR))
                chromosome = chromosome.trim().substring(0, chromosome.length() - 1);
            String[] varStr = chromosome.split(FileNameHandler.SEPARATOR);
            if (varStr.length != problem.getNumberOfVariables())
                throw new CogeeException("There is problem in the number of variables in line => " + chromosome);
            DoubleSolution s = new DefaultDoubleSolution(problem);
            for (int i = 0; i < s.getNumberOfVariables(); i++)
                s.setVariableValue(i, Double.valueOf(varStr[i]));
            front.add(s);
        }
        for (DoubleSolution solution : front) {
            problem.evaluate(solution);
        }
        return front;
    }

}
