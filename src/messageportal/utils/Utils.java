package messageportal.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Utils {
    public static final String SEND_URI = "/send";
    public static final String CREDITS_URI = "/credit";
    public static final String AUTH_URI = "/authKey";
    public static final String SENT_SMS_URI = "/sent";
    public static final String [] SEND_PARAMS = new String[]{"auth", "smsBody", "phoneNo", "id"};
    public static final String [] CREDIT_PARAMS = new String[]{"auth"};
    public static final String [] SENT_SMS_PARAMS = new String[]{"auth", "id"};
    // get API access from https://www.twilio.com/console
    public static final String TWILIO_ACCOUNT_SID = ""; //add your own
    public static final String TWILIO_AUTH_TOKEN = ""; //add your own
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String APP_VERSION = "1.0";
    public static final int SERVER_PORT = 9090;
    public static final Map<String, List<String>> ApplicationURLs = new HashMap<>();
    public static final String DB_URL_PREF = "dbURL";
    public static final String DB_USER_PREF = "dbUser";
    public static final String DB_PASS_PREF = "dbPassword";

    public static void createListOfUrlsForServer() {
        ApplicationURLs.put(SEND_URI, Arrays.asList(SEND_PARAMS));
        ApplicationURLs.put(CREDITS_URI, Arrays.asList(CREDIT_PARAMS));
        ApplicationURLs.put(SENT_SMS_URI, Arrays.asList(SENT_SMS_PARAMS));
    }

    public static String getStringFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.getDefault());
        return dateFormat.format(date);
    }
    
    public static Date getDateFromString(String stringDate) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.getDefault());
        Date date = format.parse(stringDate);
        return date;
    }
}
