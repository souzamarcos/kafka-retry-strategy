package souzamarcos.demo.core.exception;

public class ReachedMaxRetriesException extends RuntimeException{
    public ReachedMaxRetriesException(String message) {
        super(message);
    }

    public ReachedMaxRetriesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReachedMaxRetriesException(Throwable cause) {
        super(cause);
    }
}
