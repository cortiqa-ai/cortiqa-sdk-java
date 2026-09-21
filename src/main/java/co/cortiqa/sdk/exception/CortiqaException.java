package co.cortiqa.sdk.exception;

/**
 * Base exception for all errors encountered within the Cortiqa Java SDK.
 */
public class CortiqaException extends RuntimeException {
    public CortiqaException(String message) {
        super(message);
    }

    public CortiqaException(String message, Throwable cause) {
        super(message, cause);
    }
}
