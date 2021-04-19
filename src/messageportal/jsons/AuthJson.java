package messageportal.jsons;


public class AuthJson {
    private String statusCode;
    private String message;
    private String authKey;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    @Override
    public String toString() {
        return "AuthJson{" + "statusCode=" + statusCode + ", message=" + message + ", authKey=" + authKey + '}';
    }
}
