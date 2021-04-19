package messageportal.entities;

import java.io.Serializable;
import java.util.Date;

public class SmsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String receiver;
    private String sender;
    private String body;
    private Long id;
    private String status;
    private String statusMessage;
    private Date sentOn;
    private String type;

    public SmsEntity() {
    }

    public SmsEntity(String receiver, String sender, String body, Long id, String status, String statusMessage,
            Date sentOn, String type) {
        super();
        this.receiver = receiver;
        this.sender = sender;
        this.body = body;
        this.id = id;
        this.status = status;
        this.statusMessage = statusMessage;
        this.sentOn = sentOn;
        this.type = type;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Date getSentOn() {
        return sentOn;
    }

    public void setSentOn(Date sentOn) {
        this.sentOn = sentOn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((receiver == null) ? 0 : receiver.hashCode());
        result = prime * result + ((sender == null) ? 0 : sender.hashCode());
        result = prime * result + ((sentOn == null) ? 0 : sentOn.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((statusMessage == null) ? 0 : statusMessage.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
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
        SmsEntity other = (SmsEntity) obj;
        if (body == null) {
            if (other.body != null) {
                return false;
            }
        } else if (!body.equals(other.body)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (receiver == null) {
            if (other.receiver != null) {
                return false;
            }
        } else if (!receiver.equals(other.receiver)) {
            return false;
        }
        if (sender == null) {
            if (other.sender != null) {
                return false;
            }
        } else if (!sender.equals(other.sender)) {
            return false;
        }
        if (sentOn == null) {
            if (other.sentOn != null) {
                return false;
            }
        } else if (!sentOn.equals(other.sentOn)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (statusMessage == null) {
            if (other.statusMessage != null) {
                return false;
            }
        } else if (!statusMessage.equals(other.statusMessage)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SmsEntity [receiver=" + receiver + ", sender=" + sender + ", body=" + body + ", id=" + id + ", status="
                + status + ", statusMessage=" + statusMessage + ", sentOn=" + sentOn + ", type=" + type + "]";
    }
}
