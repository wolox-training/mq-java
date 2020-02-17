package wolox.training.exceptions;

public class BookIdMismatchException extends RuntimeException {
    public BookIdMismatchException(){
        super("Provided ids do not match");
    }
}
