package messageportal.jsons;


public class SMSJson {
    
    private String statusCode;
    private String message;
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SMSJson{" + "statusCode=" + statusCode + ", message=" + message + ", id=" + id + '}';
    }
}
