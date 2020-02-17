package wolox.training.exceptions;

public class IdMismatchException extends RuntimeException {
    public IdMismatchException(){
        super("Provided ids do not match");
    }
}
