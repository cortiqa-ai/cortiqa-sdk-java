package co.cortiqa.sdk.exception;

/**
 * Thrown on HTTP 404 Not Found.
 */
public class NotFoundException extends APIException {
    public NotFoundException(int statusCode, String message, String responseBody) {
        super(statusCode, message != null ? message : "Requested resource not found.", responseBody);
    }
}
