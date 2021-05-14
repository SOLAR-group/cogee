package uk.ac.ucl.cs.solar.cogee.exception;

public class CogeeException extends Exception {

    public CogeeException(String message) {
        super(message);
    }

    public CogeeException(String message,Throwable cause) {
        super(message, cause);
    }

}
