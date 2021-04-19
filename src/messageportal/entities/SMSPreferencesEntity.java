package messageportal.entities;

import java.io.Serializable;
import java.util.Objects;

public class SMSPreferencesEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    //default values
    private int invoiceDay = 15;
    private int smsCreditsLimit = 50;
    private boolean unlimitedMessaging = false;
    private String senderNr;
    private String deviceIp;
    private String devicePort = "9090";
    private boolean gateway = false;
    private String authKey;
    private int smsLeft;
    private Long id;

    public int getInvoiceDay() {
        return invoiceDay;
    }

    public void setInvoiceDay(int invoiceDay) {
        this.invoiceDay = invoiceDay;
    }

    public int getSmsCreditsLimit() {
        return smsCreditsLimit;
    }

    public void setSmsCreditsLimit(int smsCreditsLimit) {
        this.smsCreditsLimit = smsCreditsLimit;
    }

    public boolean isUnlimitedMessaging() {
        return unlimitedMessaging;
    }

    public void setUnlimitedMessaging(boolean unlimitedMessaging) {
        this.unlimitedMessaging = unlimitedMessaging;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderNr() {
        return senderNr;
    }

    public void setSenderNr(String senderNr) {
        this.senderNr = senderNr;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(String devicePort) {
        this.devicePort = devicePort;
    }

    public boolean isGateway() {
        return gateway;
    }

    public void setGateway(boolean gateway) {
        this.gateway = gateway;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public int getSmsLeft() {
        return smsLeft;
    }

    public void setSmsLeft(int smsLeft) {
        this.smsLeft = smsLeft;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.invoiceDay;
        hash = 97 * hash + this.smsCreditsLimit;
        hash = 97 * hash + (this.unlimitedMessaging ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.senderNr);
        hash = 97 * hash + Objects.hashCode(this.deviceIp);
        hash = 97 * hash + Objects.hashCode(this.devicePort);
        hash = 97 * hash + (this.gateway ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.authKey);
        hash = 97 * hash + this.smsLeft;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SMSPreferencesEntity other = (SMSPreferencesEntity) obj;
        if (this.invoiceDay != other.invoiceDay) {
            return false;
        }
        if (this.smsCreditsLimit != other.smsCreditsLimit) {
            return false;
        }
        if (this.unlimitedMessaging != other.unlimitedMessaging) {
            return false;
        }
        if (this.gateway != other.gateway) {
            return false;
        }
        if (this.smsLeft != other.smsLeft) {
            return false;
        }
        if (!Objects.equals(this.senderNr, other.senderNr)) {
            return false;
        }
        if (!Objects.equals(this.deviceIp, other.deviceIp)) {
            return false;
        }
        if (!Objects.equals(this.devicePort, other.devicePort)) {
            return false;
        }
        if (!Objects.equals(this.authKey, other.authKey)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SMSPreferencesEntity{" + "invoiceDay=" + invoiceDay + ", smsCreditsLimit=" + smsCreditsLimit + ", unlimitedMessaging=" + unlimitedMessaging + ", senderNr=" + senderNr + ", deviceIp=" + deviceIp + ", devicePort=" + devicePort + ", gateway=" + gateway + ", authKey=" + authKey + ", smsLeft=" + smsLeft + ", id=" + id + '}';
    }
}
