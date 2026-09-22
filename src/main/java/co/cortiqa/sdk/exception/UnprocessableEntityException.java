package co.cortiqa.sdk.exception;

/**
 * Exception thrown when validation fails on request parameters (HTTP 422).
 */
public class UnprocessableEntityException extends APIException {
    public UnprocessableEntityException(String message, String responseBody, String param, String code, String errorType) {
        super(422, message, responseBody, param, code, errorType);
    }
}
