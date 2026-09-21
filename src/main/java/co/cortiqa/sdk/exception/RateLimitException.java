package co.cortiqa.sdk.exception;

/**
 * Thrown on HTTP 429 Too Many Requests (rate limit reached).
 */
public class RateLimitException extends APIException {
    public RateLimitException(int statusCode, String message, String responseBody) {
        super(statusCode, message != null ? message : "Cortiqa API rate limit exceeded.", responseBody);
    }
}
