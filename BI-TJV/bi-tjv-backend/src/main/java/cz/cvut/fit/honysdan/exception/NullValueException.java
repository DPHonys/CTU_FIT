package cz.cvut.fit.honysdan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NullValueException extends RuntimeException {
    public NullValueException() {
        super();
    }
    public NullValueException(String message, Throwable cause) {
        super(message, cause);
    }
    public NullValueException(String message) {
        super(message);
    }
    public NullValueException(Throwable cause) {
        super(cause);
    }
}
