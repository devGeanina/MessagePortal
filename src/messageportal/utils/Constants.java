package messageportal.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public enum SMS_TYPE {

        GATEWAY((short) 1, "Gateway"), TWILIO((short) 2, "Twilio");

        private short type;
        private String name;
        private static final Map<String, SMS_TYPE> labelMap = new HashMap<String, Constants.SMS_TYPE>();

        static {
            for (SMS_TYPE a : values()) {
                labelMap.put(a.name, a);
            }
        }

        private SMS_TYPE(short type, String name) {
            this.type = type;
            this.name = name;
        }

        public short getType() {
            return type;
        }

        public void setType(short type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static SMS_TYPE valueOfLabel(String label) {
            return labelMap.get(label);
        }

        public static String getNameByCode(int code) {
            for (SMS_TYPE s : SMS_TYPE.values()) {
                if (code == s.type) {
                    return s.name;
                }
            }
            return null;
        }
    }

    public enum SMS_STATUS {

        SENT((short) 0, "Sent"),
        PENDING((short) 1, "Pending"),
        ERROR((short) 2, "Error");

        private short type;
        private String name;
        private static final Map<String, SMS_STATUS> labelMap = new HashMap<String, Constants.SMS_STATUS>();

        static {
            for (SMS_STATUS a : values()) {
                labelMap.put(a.name, a);
            }
        }

        private SMS_STATUS(short type, String name) {
            this.type = type;
            this.name = name;
        }

        public short getType() {
            return type;
        }

        public void setType(short type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static SMS_STATUS valueOfLabel(String label) {
            return labelMap.get(label);
        }

        public static String getNameByCode(int code) {
            for (SMS_STATUS s : SMS_STATUS.values()) {
                if (code == s.type) {
                    return s.name;
                }
            }
            return null;
        }
    }

      public enum RESPONSE_STATUS {
        SENDING((short) 0, "Sending"),
        AUTH_FAILURE((short) 1, "Authentication failure"),
        INSUFFICIENT_CREDIT((short) 2, "Insufficient credit"),
        SUFFICIENT_CREDIT((short) 3, "Sufficient credit"),
        INVALID_URI((short) 4, "Invalid uri"),
        INVALID_PARAMS((short) 5, "Invalid parameters"),
        SENT((short) 6, "Sent"),
        SENT_ERROR((short) 7, "Sent error"),
        AUTH_FOUND((short) 8, "Authentication key found"),
        AUTH_NOT_FOUND((short) 9, "Authentication key not set");

        private short type;
        private String name;
        private static final Map<String, RESPONSE_STATUS> labelMap = new HashMap<String, RESPONSE_STATUS>();

        static {
            for (RESPONSE_STATUS a : values()) {
                labelMap.put(a.name, a);
            }
        }

        private RESPONSE_STATUS(short type, String name) {
            this.type = type;
            this.name = name;
        }

        public short getType() {
            return type;
        }

        public void setType(short type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static RESPONSE_STATUS valueOfLabel(String label) {
            return labelMap.get(label);
        }

        public static String getNameByCode(int code) {
            for (RESPONSE_STATUS r : RESPONSE_STATUS.values()) {
                if (code == r.type) {
                    return r.name;
                }
            }
            return null;
        }
    }
}
