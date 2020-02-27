package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class IdMismatchException extends RuntimeException {
    public IdMismatchException(String identifier){
        super(String.format("the %s's id does not match the path variable id", identifier));
    }
}
