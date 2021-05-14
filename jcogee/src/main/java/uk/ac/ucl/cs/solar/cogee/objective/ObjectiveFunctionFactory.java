package uk.ac.ucl.cs.solar.cogee.objective;

import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;

public class ObjectiveFunctionFactory {

    public enum ObjectiveFunction {
        SAE,
        SOE,
        SUE,
        CI,
        MAE
    }

    public static SEEObjectiveFunction getObjectiveFunction(ObjectiveFunction functionName) throws CogeeException {
        switch (functionName) {
            case SAE:
                return SumOfAbsoluteErrors.getInstance();
            case SOE:
                return SumOfOverestimates.getInstance();
            case SUE:
                return SumOfUnderestimates.getInstance();
            case CI:
                return ConfidenceInterval.getInstance();
            case MAE:
                return MeanAbsoluteError.getInstance();

        }
        throw new CogeeException("Unknown Objected Function <" + functionName + "> Requested!");
    }


}
