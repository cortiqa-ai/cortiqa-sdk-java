package co.cortiqa.sdk.exception;

/**
 * Thrown on HTTP 401 Unauthorized (invalid or missing API key).
 */
public class AuthenticationException extends APIException {
    public AuthenticationException(int statusCode, String message, String responseBody) {
        super(statusCode, message != null ? message : "Invalid or missing Cortiqa API key.", responseBody);
    }
}
