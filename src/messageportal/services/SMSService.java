package messageportal.services;

import com.google.gson.Gson;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import messageportal.entities.SmsEntity;
import messageportal.utils.Constants;
import messageportal.utils.Utils;
import messageportal.entities.SMSPreferencesEntity;
import messageportal.jsons.AuthJson;
import messageportal.jsons.CreditsJson;
import messageportal.jsons.SMSJson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SMSService {

    private DatabaseService dbService = new DatabaseService();
    private static final Logger LOGGER = LogManager.getLogger(SMSService.class);

    public SMSService() {
    }

    public SMSService(DatabaseService databaseService) {
        if(dbService == null)
            dbService = databaseService;
    }

    /*
    In order to use Twilio, we need a Twilio account (Trial also available with one trial number offered) to use as the sender number.
    Verify the receiving number in Twilio.
    Change also the account SID and TOKEN in the Utils class.
     */
    public SmsEntity sendTwilioSMS(SmsEntity smsEntity, SMSPreferencesEntity sPreferencesEntity) {
        if (smsEntity == null) {
            throw new IllegalArgumentException("Null SMS object received to be sent.");
        }

        Twilio.init(Utils.TWILIO_ACCOUNT_SID, Utils.TWILIO_AUTH_TOKEN);
        Message message = Message.creator(new PhoneNumber(smsEntity.getReceiver()),
                new PhoneNumber(smsEntity.getSender()), // to - the twilio nr bought
                smsEntity.getBody()).create();

        // send SMS
        switch (message.getStatus()) {
            case SENT:
            case DELIVERED:
            case RECEIVED:
                smsEntity.setStatus(Constants.SMS_STATUS.SENT.getName());
                smsEntity.setStatusMessage("SMS sent");
                smsEntity.setSentOn(Calendar.getInstance().getTime());
                break;
            case QUEUED:
            case SENDING:
            case PARTIALLY_DELIVERED:
            case SCHEDULED:
            case RECEIVING:
                smsEntity.setStatus(Constants.SMS_STATUS.PENDING.getName());
                smsEntity.setStatusMessage("SMS pending");
                break;
            case FAILED:
            case UNDELIVERED:
                smsEntity.setStatus(Constants.SMS_STATUS.ERROR.getName());
                smsEntity.setStatusMessage("SMS failed");
                break;
            default:
                break;
        }
        smsEntity.setType(Constants.SMS_TYPE.TWILIO.getName());
        if(sPreferencesEntity != null)
            dbService.updateSMSLeft(sPreferencesEntity.getSmsLeft() - 1);
        return smsEntity;
    }

    /*
    Send SMS using a SMS Gateway on the Android phone used for the process.
    URL format e.g: http://deviceIP:devicePort/send?auth=test&smsBody=testBody&phoneNo=32434234243
     */
    public SmsEntity sendGatewaySMS(SmsEntity smsEntity, SMSPreferencesEntity savedPreferences) {
        if (smsEntity == null) {
            throw new IllegalArgumentException("Null SMS object received to be sent.");
        }
        
        smsEntity.setType(Constants.SMS_TYPE.GATEWAY.getName());
        smsEntity.setStatus(Constants.SMS_STATUS.PENDING.getName());
        smsEntity.setStatusMessage("SMS pending");
        Long smsId = dbService.saveSms(smsEntity);

        String deviceIP = savedPreferences.getDeviceIp();
        String devicePort = savedPreferences.getDevicePort();
        String authKey = "";
        AuthJson authJson = new AuthJson();
        authJson = retrieveAuthKey(deviceIP, devicePort);
        authKey = authJson.getAuthKey();
        
        if(authKey != null || (authKey != null &&!authKey.equals(""))){
            StringBuilder urlBuilder = new StringBuilder();
            URL url;
            try {
                String smsBodyEncoded = URLEncoder.encode(smsEntity.getBody(), "UTF-8");
                String authKeyEncoded = URLEncoder.encode(authKey.trim(), "UTF-8");
                String phoneNoEncoded = URLEncoder.encode(smsEntity.getReceiver().trim(), "UTF-8");
                
                urlBuilder.append("http://").append(deviceIP).append(":").append(devicePort).append(Utils.SEND_URI).append("?").append("auth=")
                        .append(authKeyEncoded).append("&smsBody=").append(smsBodyEncoded).append("&phoneNo=").append(phoneNoEncoded);
                
                if(smsId != null){ //if the client wants to track the sms by id
                    urlBuilder.append("&id=").append(smsId);
                }
                
                url = new URL(urlBuilder.toString());
                URLConnection connection = url.openConnection();
                try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                  StringBuilder response = new StringBuilder();
                  String responseLine = null;
                  while ((responseLine = br.readLine()) != null) {
                      response.append(responseLine.trim());
                  }
                  Gson gson = new Gson();
                  SMSJson smsJson = gson.fromJson(response.toString(), SMSJson.class);
                  smsEntity.setStatus(smsJson.getStatusCode());
                  smsEntity.setStatusMessage(smsJson.getMessage());
                  smsId = smsJson.getId();
                  if(Short.parseShort(smsJson.getStatusCode()) == Constants.RESPONSE_STATUS.SENT.getType())
                      smsEntity.setStatus(Constants.SMS_STATUS.SENT.getName());
                  else if(Short.parseShort(smsJson.getStatusCode()) == Constants.RESPONSE_STATUS.SENDING.getType())
                      smsEntity.setStatus(Constants.SMS_STATUS.PENDING.getName());
                  else
                      smsEntity.setStatus(Constants.SMS_STATUS.ERROR.getName());
                  
                  if((smsEntity.getStatus().equalsIgnoreCase(Constants.SMS_STATUS.PENDING.getName())) && smsJson.getId() != null){
                      isSMSSent(deviceIP, devicePort, authKey, smsId);
                  }
              }
            } catch (MalformedURLException ex) {
                LOGGER.error("Exception creating the sending URL: " + ex.getMessage());
            } catch (IOException ex) {
                LOGGER.error("Exception creating the URL connection: " + ex.getMessage());
            }
            
            //update credit info
            retrieveCredits(deviceIP, devicePort, authKey);
        } else{
            smsEntity.setStatus(Constants.SMS_STATUS.ERROR.getName());
            smsEntity.setStatusMessage(Constants.RESPONSE_STATUS.AUTH_FAILURE.getName());
        }
        
        return smsEntity;
    }
    
    private void retrieveCredits(String deviceIP, String devicePort, String authKey){
      Service<Void> service = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {           
                            @Override
                            protected Void call() throws Exception {
                                //Background work                       
                                final CountDownLatch latch = new CountDownLatch(1);
                                Platform.runLater(new Runnable() {                          
                                    @Override
                                    public void run() {
                                        try{
                                            StringBuilder urlBuilder = new StringBuilder();
                                            CreditsJson credits = new CreditsJson();
                                            URL url;
                                                try {
                                                    String authKeyEncoded = URLEncoder.encode(authKey.trim(), "UTF-8");
                                                    urlBuilder.append("http://").append(deviceIP).append(":").append(devicePort).append(Utils.CREDITS_URI).append("?").append("auth=").append(authKeyEncoded);
                                                    url = new URL(urlBuilder.toString());
                                                    URLConnection connection = url.openConnection();
                                                    try(BufferedReader br = new BufferedReader(
                                                        new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                                                          StringBuilder response = new StringBuilder();
                                                          String responseLine = null;
                                                          while ((responseLine = br.readLine()) != null) {
                                                              response.append(responseLine.trim());
                                                          }
                                                          Gson gson = new Gson();
                                                          credits = gson.fromJson(response.toString(), CreditsJson.class);
                                                          if(credits != null){
                                                            boolean isUnlimited = false;
                                                            if(credits.getUnlimited() == 1)
                                                              isUnlimited = true;
                                                            updateCredits(credits.getInvoiceDay(),credits.getCreditsLimit(), isUnlimited, credits.getSmsLeft());
                                                          }
                                                    }
                                                } catch (MalformedURLException ex) {
                                                    LOGGER.error("Exception creating the sending URL: " + ex.getMessage());
                                                } catch (IOException ex) {
                                                    LOGGER.error("Exception creating the URL connection: " + ex.getMessage());
                                                }

                                            }finally{
                                                latch.countDown();
                                            }
                                        }
                                      });
                          latch.await();                      
                          return null;
                      }
                  };
              }
          };
          service.start();
    }
    
    private void isSMSSent(String deviceIP, String devicePort, String authKey, Long id){
    Timer timer = new java.util.Timer();
    timer.schedule(new TimerTask() {
    public void run() {
         Platform.runLater(new Runnable() {
            public void run() {
                StringBuilder urlBuilder = new StringBuilder();
                SMSJson sentSMS = new SMSJson();
                URL url;
                    try {
                        String authKeyEncoded = URLEncoder.encode(authKey.trim(), "UTF-8");
                        urlBuilder.append("http://").append(deviceIP).append(":").append(devicePort).append(Utils.SENT_SMS_URI).append("?").append("auth=").append(authKeyEncoded).append("&id=").append(String.valueOf(id));
                        url = new URL(urlBuilder.toString());
                        URLConnection connection = url.openConnection();
                        try(BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                              StringBuilder response = new StringBuilder();
                              String responseLine = null;
                              while ((responseLine = br.readLine()) != null) {
                                  response.append(responseLine.trim());
                              }
                              Gson gson = new Gson();
                              sentSMS = gson.fromJson(response.toString(), SMSJson.class);
                              if(sentSMS != null){
                                   short smsStatusCode = Constants.SMS_STATUS.PENDING.getType();
                                   if(Short.parseShort(sentSMS.getStatusCode()) == Constants.RESPONSE_STATUS.SENT.getType())
                                        smsStatusCode = Constants.SMS_STATUS.SENT.getType();
                                   else if(Short.parseShort(sentSMS.getStatusCode()) == Constants.RESPONSE_STATUS.SENT_ERROR.getType())
                                        smsStatusCode = Constants.SMS_STATUS.ERROR.getType();

                                   updateSMSStatus(smsStatusCode, sentSMS.getMessage(), id);
                              }
                        }
                    } catch (MalformedURLException ex) {
                        LOGGER.error("Exception creating the sending URL: " + ex.getMessage());
                    } catch (IOException ex) {
                        LOGGER.error("Exception creating the URL connection: " + ex.getMessage());
                    } 

                }
            });
        }
        }, 120000); //wait 2 min before checking
    }
    
    private AuthJson retrieveAuthKey(String deviceIP, String devicePort) {
    StringBuilder urlBuilder = new StringBuilder();
    AuthJson authJson = new AuthJson();
    URL url;
        try {
            urlBuilder.append("http://").append(deviceIP).append(":").append(String.valueOf(devicePort)).append(Utils.AUTH_URI);
            url = new URL(urlBuilder.toString());
            URLConnection connection = url.openConnection();
            try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                  StringBuilder response = new StringBuilder();
                  String responseLine = null;
                  while ((responseLine = br.readLine()) != null) {
                      response.append(responseLine.trim());
                  }
                  Gson gson = new Gson();
                  authJson = gson.fromJson(response.toString(), AuthJson.class);
                  if(Short.parseShort(authJson.getStatusCode()) == Constants.RESPONSE_STATUS.AUTH_FOUND.getType())
                    saveAuthKey(authJson.getAuthKey());
              }
        } catch (MalformedURLException ex) {
            LOGGER.error("Exception creating the sending URL: " + ex.getMessage());
        } catch (IOException ex) {
            LOGGER.error("Exception creating the URL connection: " + ex.getMessage());
        }
        return authJson;
    }

    public boolean isSMSSent(String id) {
        if (id == null) {
            return false;
        } else {
            String smsStatus = dbService.getSMSStatusById(id);
            if (smsStatus != null) {
                if (smsStatus.equals(Constants.SMS_STATUS.SENT.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSmsStatusMessageFromDB(String id) {
        if (id == null) {
            return null;
        }
        return dbService.getSMSStatusMessageById(id);
    }

    public String getSMSStatusFromDB(String id) {
        if (id == null) {
            return null;
        }

        return dbService.getSMSStatusById(id);
    }

    public boolean isSMSLimitSurpassed() {
        SMSPreferencesEntity smsPreferencesEntity = dbService.getSmsPreferences();
        if (smsPreferencesEntity.getInvoiceDay() == 0) {
            return false;
        }

        if (smsPreferencesEntity.isUnlimitedMessaging() || smsPreferencesEntity.getSmsCreditsLimit() >= 0) {
            return false;
        }

        List<SmsEntity> sentSMSFromTheLastInvoice = dbService
                .getSentSMSFromTheLastInvoice(smsPreferencesEntity.getInvoiceDay());
        if (sentSMSFromTheLastInvoice == null) {
            return false;
        }

        if (sentSMSFromTheLastInvoice.size() <= 0
                || sentSMSFromTheLastInvoice.size() >= smsPreferencesEntity.getSmsCreditsLimit()) {
            return true;
        }
        return false;
    }

    public int smsMessagesLeftToSend() {
        SMSPreferencesEntity smsPreferencesEntity = dbService.getSmsPreferences();
        int smsLimit = smsPreferencesEntity.getSmsCreditsLimit();
        List<SmsEntity> sentSMSFromTheLastInvoice = dbService
                .getSentSMSFromTheLastInvoice(smsPreferencesEntity.getInvoiceDay());
        if (sentSMSFromTheLastInvoice != null) {
            int smsLeftToSend = smsLimit - sentSMSFromTheLastInvoice.size();
            return smsLeftToSend;
        }
        return 0;
    }

    public boolean isCreditsUnlimited() {
        SMSPreferencesEntity smsPreferencesEntity = dbService.getSmsPreferences();
        if (smsPreferencesEntity.isUnlimitedMessaging()) {
            return true;
        }
        return false;
    }

    public List<SmsEntity> getAllSMSList() {
        List<SmsEntity> smsEntities = dbService.getAllSmsList();
        return smsEntities;
    }
    
    public List<SmsEntity> getSentSMSList() {
        List<SmsEntity> smsEntities = dbService.getSentSmsList();
        return smsEntities;
    }

    public List<SmsEntity> getErrorSMSList() {
        List<SmsEntity> smsEntities = dbService.getErrorSmsList();
        return smsEntities;
    }

    public List<SmsEntity> getGatewaySMSList() {
        List<SmsEntity> smsEntities = dbService.getGatewaySmsList();
        return smsEntities;
    }

     public List<SmsEntity> getTwilioSMSList() {
        List<SmsEntity> smsEntities = dbService.getTwilioSmsList();
        return smsEntities;
    }
     
     public List<SmsEntity> getSMSList(String selectionType){
         List<SmsEntity> smsList = new ArrayList<>();
         switch (selectionType){
             case "All":
                 smsList = getAllSMSList();
                 break;
             case "Sent":
                 smsList = getSentSMSList();
                 break;
             case "Failed":
                 smsList =getErrorSMSList();
                 break;
             case "Gateway":
                 smsList = getGatewaySMSList();
                 break;
             case "Twilio":
                 smsList = getTwilioSMSList();
                 break;
             default:
                 //nothing to do
         }
         return smsList;
     }

    public boolean sendSMS(SmsEntity sms, SMSPreferencesEntity savedPreferences) {
        if (sms == null) {
            throw new IllegalArgumentException("Cannot send the SMS because it's null.");
        }
        if (savedPreferences == null) {
            throw new IllegalArgumentException("Cannot send the SMS because the settings are not saved.");
        }
        if (!savedPreferences.isGateway()) {
            sms = sendTwilioSMS(sms, savedPreferences);
            dbService.saveSms(sms);
        } else {
            sms = sendGatewaySMS(sms, savedPreferences);
        }

        if (sms.getStatus().equalsIgnoreCase(Constants.SMS_STATUS.SENT.getName()) || sms.getStatus().equalsIgnoreCase(Constants.SMS_STATUS.PENDING.getName())) {
            return true;
        } else {
            return false;
        }
    }

    public void saveSMS(SmsEntity sms) {
        if (sms == null) {
            throw new IllegalArgumentException("Cannot save the SMS because it's null.");
        }
        dbService.saveSms(sms);
    }

    public void saveSMSPref(SMSPreferencesEntity smsPref) {
        if (smsPref == null) {
            throw new IllegalArgumentException("Cannot save the item because it's null.");
        }
        if (smsPref.getId() != null) {
            dbService.updateSmsPref(smsPref);
        } else {
            dbService.saveSmsPref(smsPref);
        }
    }

    public void deleteSMS(Long smsId) {
        if (smsId == null) {
            throw new IllegalArgumentException("Cannot delete the item because the id is null.");
        }
        dbService.deleteSMS(smsId);
    }

    public void createDBSchema() {
        dbService.createDBSchema();
    }

    public SMSPreferencesEntity getSavedPreferences() {
        return dbService.getSmsPreferences();
    }

    public String getSenderNo() {
        return dbService.getSenderNo();
    }
    
    public void saveAuthKey(String authKey){
          if (authKey == null) {
            throw new IllegalArgumentException("Cannot save the authKey because it is null.");
        }
        dbService.saveAuthKey(authKey);
    }
    
    public void updateCredits(int invoiceDay, int smsLimit, boolean unlimited, int smsLeft){
        dbService.updateCredits(invoiceDay, smsLimit, unlimited, smsLeft);
    }
    
    public void updateSMSStatus(short smsStatus, String smsMgs, Long id) {
        dbService.updateSMSStatus(smsStatus, smsMgs, id);
    }
}
