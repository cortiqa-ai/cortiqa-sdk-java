package co.cortiqa.sdk.exception;

/**
 * Thrown on HTTP 403 Forbidden (permission denied or account restricted).
 */
public class PermissionDeniedException extends APIException {
    public PermissionDeniedException(int statusCode, String message, String responseBody) {
        super(statusCode, message != null ? message : "Permission denied.", responseBody);
    }
}
