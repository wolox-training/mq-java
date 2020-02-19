package wolox.training.exceptions;

public class IdMismatchException extends RuntimeException {

    public IdMismatchException(String identifier){
        super(String.format("the %s's id does not match the path variable id", identifier));
    }

}
