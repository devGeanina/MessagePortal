package messageportal.jsons;

public class CreditsJson {
    private String statusCode;
    private String message;
    private int smsLeft;
    private int creditsLimit;
    private int invoiceDay;
    private int unlimited; // false 0 - true 1

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

    public int getSmsLeft() {
        return smsLeft;
    }

    public void setSmsLeft(int smsLeft) {
        this.smsLeft = smsLeft;
    }

    public int getCreditsLimit() {
        return creditsLimit;
    }

    public void setCreditsLimit(int creditsLimit) {
        this.creditsLimit = creditsLimit;
    }

    public int getInvoiceDay() {
        return invoiceDay;
    }

    public void setInvoiceDay(int invoiceDay) {
        this.invoiceDay = invoiceDay;
    }

    public int getUnlimited() {
        return unlimited;
    }

    public void setUnlimited(int unlimited) {
        this.unlimited = unlimited;
    }

    @Override
    public String toString() {
        return "CreditsJson{" + "statusCode=" + statusCode + ", message=" + message + ", smsLeft=" + smsLeft + ", creditsLimit=" + creditsLimit + ", invoiceDay=" + invoiceDay + ", unlimited=" + unlimited + '}';
    }
}
