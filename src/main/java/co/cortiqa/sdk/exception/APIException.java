package co.cortiqa.sdk.exception;

/**
 * Exception thrown when the Cortiqa API returns an error response.
 */
public class APIException extends CortiqaException {
    private final int statusCode;
    private final String responseBody;

    public APIException(int statusCode, String message, String responseBody) {
        super(message != null ? message : "Cortiqa API error with HTTP " + statusCode);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
