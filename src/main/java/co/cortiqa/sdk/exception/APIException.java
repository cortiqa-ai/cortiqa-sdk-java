package co.cortiqa.sdk.exception;

/**
 * Exception thrown when the Cortiqa API returns an error response.
 */
public class APIException extends CortiqaException {
    private final int statusCode;
    private final String responseBody;
    private final String param;
    private final String code;
    private final String errorType;

    public APIException(int statusCode, String message, String responseBody) {
        this(statusCode, message, responseBody, null, null, null);
    }

    public APIException(int statusCode, String message, String responseBody, String param, String code, String errorType) {
        super(formatMessage(statusCode, message, param));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.param = param;
        this.code = code;
        this.errorType = errorType;
    }

    private static String formatMessage(int statusCode, String message, String param) {
        if (message == null || message.isEmpty()) {
            message = "Cortiqa API error with HTTP " + statusCode;
        }
        if (param != null && !param.isEmpty() && !message.contains(param)) {
            return "[" + param + "] " + message;
        }
        return message;
    }

    public int getStatusCode() { return statusCode; }
    public String getResponseBody() { return responseBody; }
    public String getParam() { return param; }
    public String getCode() { return code; }
    public String getErrorType() { return errorType; }
}
