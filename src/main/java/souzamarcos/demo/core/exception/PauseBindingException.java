package souzamarcos.demo.core.exception;

public class PauseBindingException extends RuntimeException{
    public PauseBindingException(String message) {
        super(message);
    }

    public PauseBindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PauseBindingException(Throwable cause) {
        super(cause);
    }
}
