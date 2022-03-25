package cz.cvut.fit.honysdan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MoveKnowledgeException extends RuntimeException {
    public MoveKnowledgeException() {
        super();
    }
    public MoveKnowledgeException(String message, Throwable cause) {
        super(message, cause);
    }
    public MoveKnowledgeException(String message) {
        super(message);
    }
    public MoveKnowledgeException(Throwable cause) {
        super(cause);
    }
}
