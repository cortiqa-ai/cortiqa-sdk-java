package co.cortiqa.sdk.exception;

/**
 * Exception thrown when the request parameters are invalid (HTTP 400).
 */
public class BadRequestException extends APIException {
    public BadRequestException(String message, String responseBody, String param, String code, String errorType) {
        super(400, message, responseBody, param, code, errorType);
    }
}
