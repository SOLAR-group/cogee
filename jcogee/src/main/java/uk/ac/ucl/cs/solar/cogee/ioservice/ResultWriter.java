package uk.ac.ucl.cs.solar.cogee.ioservice;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import uk.ac.ucl.cs.solar.cogee.FileNameHandler;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;
import uk.ac.ucl.cs.solar.cogee.objective.ObjectiveFunctionFactory;

import java.util.ArrayList;
import java.util.List;

public class ResultWriter {

    public static void writeParetoFront(List<? extends Solution> front, String fullName) throws CogeeException{
        writeParetoFront(front, fullName, null);
    }

    public static void writeParetoFront(List<? extends Solution> front, String fullName, List<ObjectiveFunctionFactory.ObjectiveFunction> objectives) throws CogeeException{
        if (front == null)
            throw new CogeeException("The input pareto front is null!");
        List<String> table = new ArrayList<>(front.size());
        int numberOfObjectives;
        if(objectives!=null && objectives.size()>0) {
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < objectives.size(); i++) {
                header.append(objectives.get(i).name() +FileNameHandler.SEPARATOR);
            }
            table.add(header.toString());
            numberOfObjectives = objectives.size();
        }else if(front.size()>0)
            numberOfObjectives = front.get(0).getNumberOfObjectives();
        else
            numberOfObjectives = 0;
        for (Solution solution : front) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < numberOfObjectives; i++) {
                str.append(solution.getObjective(i) + FileNameHandler.SEPARATOR);
            }
            str.deleteCharAt(str.length()-1);
            table.add(str.toString());
        }
        TextWriter.write(table, fullName);
    }

    public static void writeSolutions(List<? extends Solution> front, String fullName) throws CogeeException{
        if (front == null)
            throw new CogeeException("The input pareto front is null!");
        if (front.size() == 0)
            return;
        List<String> table = new ArrayList<>(front.size());
        int numberOfVariables = front.get(0).getNumberOfVariables();
        for (Solution solution : front) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < numberOfVariables; i++) {
                str.append(solution.getVariableValue(i) + FileNameHandler.SEPARATOR);
            }
            str.deleteCharAt(str.length()-1);
            table.add(str.toString());
        }
        TextWriter.write(table, fullName);
    }
}
