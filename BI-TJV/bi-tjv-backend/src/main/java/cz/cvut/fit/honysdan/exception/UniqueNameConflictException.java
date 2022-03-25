package cz.cvut.fit.honysdan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UniqueNameConflictException extends RuntimeException {
    public UniqueNameConflictException() {
        super();
    }
    public UniqueNameConflictException(String message, Throwable cause) {
        super(message, cause);
    }
    public UniqueNameConflictException(String message) {
        super(message);
    }
    public UniqueNameConflictException(Throwable cause) {
        super(cause);
    }
}
