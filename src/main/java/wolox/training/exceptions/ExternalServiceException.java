package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE)
public class ExternalServiceException extends Exception {

    private Exception exception;

    private Class thrownBy;

    private int code;

    public ExternalServiceException(Class thrownBy, Exception exception) {
        this.thrownBy = thrownBy;
        this.exception = exception;
    }

    public ExternalServiceException(Class thrownBy, int Code) {
        this.thrownBy = thrownBy;
        this.code = code;
    }

    @Override
    public String toString() {
        if (exception != null) {
            return String.format("%s threw %s", thrownBy.getName(), exception.toString());
        } else {
            return String.format("%s got HTTP code %d", thrownBy.getName(), code);
        }
    }
}
