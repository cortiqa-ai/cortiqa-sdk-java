package co.cortiqa.sdk.exception;

/**
 * Thrown on HTTP 500+ Internal Server Error.
 */
public class InternalServerException extends APIException {
    public InternalServerException(int statusCode, String message, String responseBody) {
        super(statusCode, message != null ? message : "Cortiqa internal server error.", responseBody);
    }
}
