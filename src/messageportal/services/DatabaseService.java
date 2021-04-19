package messageportal.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import messageportal.DatabaseSettingsController;
import messageportal.utils.Constants;
import messageportal.utils.Utils;
import messageportal.entities.SMSPreferencesEntity;
import messageportal.entities.SmsEntity;
import messageportal.utils.ToastMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseService {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseService.class);

    public void createDBSchema() {
        System.out.println("messageportal.services.SMSDAOImpl.createDBSchema()");
        String createIDSeq = "CREATE SEQUENCE IF NOT EXISTS id_seq"
                + "    START WITH 1"
                + "    INCREMENT BY 10"
                + "    NO MINVALUE"
                + "    NO MAXVALUE"
                + "    CACHE 1";
        String createSMSTable = "CREATE TABLE IF NOT EXISTS sms(id bigint"
                + "    DEFAULT nextval('id_seq'::regclass) NOT NULL,"
                + "    type smallint NOT NULL,"
                + "    sender varchar(60),"
                + "    receiver varchar(60) NOT NULL,"
                + "    body varchar(255) NOT NULL,"
                + "    status smallint NOT NULL,"
                + "    status_msg varchar(255) NOT NULL,"
                + "    sent_on date NOT NULL,"
                + "    version bigint DEFAULT 0 NOT NULL)";

        String createSMSPrefTable = "CREATE TABLE IF NOT EXISTS sms_pref(id bigint"
                + "    DEFAULT nextval('id_seq'::regclass) NOT NULL,"
                + "    invoice_day integer NOT NULL,"
                + "    credits_limit integer NOT NULL,"
                + "    sender varchar(60),"
                + "    unlimited boolean NOT NULL,"
                + "    device_ip varchar(60),"
                + "    device_port varchar(6),"
                + "    gateway boolean NOT NULL,"
                + "    auth_key varchar(100),"
                + "    sms_left integer,"
                + "    version bigint DEFAULT 0 NOT NULL)";

        String changeSMSTbOwner = "ALTER TABLE sms OWNER TO postgres";
        String changeSMSPrefTbOwner = "ALTER TABLE sms_pref OWNER TO postgres";
        Map<String, String> dbSettings = getDBConnection();
        
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                Statement st = con.createStatement();) {
            st.executeUpdate(createIDSeq);
            st.executeUpdate(createSMSTable);
            st.executeUpdate(createSMSPrefTable);
            st.executeUpdate(changeSMSTbOwner);
            st.executeUpdate(changeSMSPrefTbOwner);
        } catch (SQLException ex) {
            LOGGER.error("Exception creating the db schema: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }else{
                ToastMessage.makeText("Can't create database, check settings.");
            }
        }
    }

    public List<SmsEntity> getAllSmsList() {
        List<SmsEntity> smsList = new ArrayList<>();
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM sms ORDER BY sent_on DESC")) {

            while (rs.next()) {
                SmsEntity entity = new SmsEntity();
                entity.setId(rs.getLong(1));
                short type = rs.getShort(2);
                entity.setType(Constants.SMS_TYPE.getNameByCode(type));
                if(rs.getString(3) != null)
                    entity.setSender(rs.getString(3));
                entity.setReceiver(rs.getString(4));
                entity.setBody(rs.getString(5));
                short status = rs.getShort(6);
                entity.setStatus(Constants.SMS_STATUS.getNameByCode(status));
                entity.setStatusMessage(rs.getString(7));
                entity.setSentOn(rs.getDate(8));
                smsList.add(entity);
            }

        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the SMS list: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return smsList;
    }
    
    public List<SmsEntity> getSentSmsList() {
        List<SmsEntity> smsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM sms WHERE status = ? ORDER BY sent_on DESC";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement st = con.prepareStatement(selectQuery)) {
                st.setShort(1, Constants.SMS_STATUS.SENT.getType());
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        SmsEntity entity = new SmsEntity();
                        entity.setId(rs.getLong(1));
                        short type = rs.getShort(2);
                        entity.setType(Constants.SMS_TYPE.getNameByCode(type));
                        if(rs.getString(3) != null)
                            entity.setSender(rs.getString(3));
                        entity.setReceiver(rs.getString(4));
                        entity.setBody(rs.getString(5));
                        short status = rs.getShort(6);
                        entity.setStatus(Constants.SMS_STATUS.getNameByCode(status));
                        entity.setStatusMessage(rs.getString(7));
                        entity.setSentOn(rs.getDate(8));
                        smsList.add(entity);
                    }
                }
            }

        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the sent SMS list: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return smsList;
    }
    
     public List<SmsEntity> getErrorSmsList() {
        List<SmsEntity> smsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM sms WHERE status = ? ORDER BY sent_on DESC";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement st = con.prepareStatement(selectQuery)) {
                st.setShort(1, Constants.SMS_STATUS.ERROR.getType());
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        SmsEntity entity = new SmsEntity();
                        entity.setId(rs.getLong(1));
                        short type = rs.getShort(2);
                        entity.setType(Constants.SMS_TYPE.getNameByCode(type));
                        if(rs.getString(3) != null)
                            entity.setSender(rs.getString(3));
                        entity.setReceiver(rs.getString(4));
                        entity.setBody(rs.getString(5));
                        short status = rs.getShort(6);
                        entity.setStatus(Constants.SMS_STATUS.getNameByCode(status));
                        entity.setStatusMessage(rs.getString(7));
                        entity.setSentOn(rs.getDate(8));
                        smsList.add(entity);
                    }
                }
            }

        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the error SMS list: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return smsList;
    }
     
     public List<SmsEntity> getTwilioSmsList() {
        List<SmsEntity> smsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM sms WHERE type = ? ORDER BY sent_on DESC";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement st = con.prepareStatement(selectQuery)) {
                st.setShort(1, Constants.SMS_TYPE.TWILIO.getType());
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        SmsEntity entity = new SmsEntity();
                        entity.setId(rs.getLong(1));
                        short type = rs.getShort(2);
                        entity.setType(Constants.SMS_TYPE.getNameByCode(type));
                        if(rs.getString(3) != null)
                            entity.setSender(rs.getString(3));
                        entity.setReceiver(rs.getString(4));
                        entity.setBody(rs.getString(5));
                        short status = rs.getShort(6);
                        entity.setStatus(Constants.SMS_STATUS.getNameByCode(status));
                        entity.setStatusMessage(rs.getString(7));
                        entity.setSentOn(rs.getDate(8));
                        smsList.add(entity);
                    }
                }
            }

        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the twilio SMS list: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return smsList;
    }
     
      public List<SmsEntity> getGatewaySmsList() {
        List<SmsEntity> smsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM sms WHERE type = ? ORDER BY sent_on DESC";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement st = con.prepareStatement(selectQuery)) {
                st.setShort(1, Constants.SMS_TYPE.GATEWAY.getType());
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        SmsEntity entity = new SmsEntity();
                        entity.setId(rs.getLong(1));
                        short type = rs.getShort(2);
                        entity.setType(Constants.SMS_TYPE.getNameByCode(type));
                        if(rs.getString(3) != null)
                            entity.setSender(rs.getString(3));
                        entity.setReceiver(rs.getString(4));
                        entity.setBody(rs.getString(5));
                        short status = rs.getShort(6);
                        entity.setStatus(Constants.SMS_STATUS.getNameByCode(status));
                        entity.setStatusMessage(rs.getString(7));
                        entity.setSentOn(rs.getDate(8));
                        smsList.add(entity);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the twilio SMS list: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return smsList;
    }

    public String getSMSStatusMessageById(String id) {
        String smsStatus = "";
        String selectStatusMsgSql = "SELECT status_msg FROM sms WHERE id = ?";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement st = con.prepareStatement(selectStatusMsgSql)) {
                st.setLong(1, Long.valueOf(id));
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        smsStatus = rs.getString(1);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the SMS status message for id " + id + ". Excepion: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return smsStatus;
    }

    public String getSMSStatusById(String id) {
        String smsStatus = "";
        String selectStatusSql = "SELECT status FROM sms WHERE id = ?";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement st = con.prepareStatement(selectStatusSql)) {
                st.setLong(1, Long.valueOf(id));
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        short status = rs.getShort(1);
                        smsStatus = Constants.SMS_STATUS.getNameByCode(status);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the SMS status for id " + id + ". Excepion: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return smsStatus;
    }

    public List<SmsEntity> getSentSMSFromTheLastInvoice(int invoiceDay) {
        List<SmsEntity> smsList = new ArrayList<>();
        Date currentMonth = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, invoiceDay);
        Date lastInvoiceDay = calendar.getTime();
        String selectQuery = "SELECT * FROM conectys_sms WHERE DATE(sent_date) BETWEEN '" + Utils.getStringFromDate(lastInvoiceDay) + "' AND '" + Utils.getStringFromDate(currentMonth) + "' AND status = '" + Constants.SMS_STATUS.SENT.getName() + "' ORDER BY sent_date DESC";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(selectQuery)) {

            while (rs.next()) {
                SmsEntity entity = new SmsEntity();
                entity.setId(rs.getLong(1));
                short type = rs.getShort(2);
                entity.setType(Constants.SMS_TYPE.getNameByCode(type));
                entity.setSender(rs.getString(3));
                entity.setReceiver(rs.getString(4));
                entity.setBody(rs.getString(5));
                short status = rs.getShort(6);
                entity.setStatus(Constants.SMS_STATUS.getNameByCode(status));
                entity.setStatusMessage(rs.getString(7));
                entity.setSentOn(rs.getDate(8));
                smsList.add(entity);
            }

        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the SMS list: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return smsList;
    }

    public SMSPreferencesEntity getSmsPreferences() {
        SMSPreferencesEntity preferencesEntity = new SMSPreferencesEntity();
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM sms_pref")) {

            List<SMSPreferencesEntity> smsPref = new ArrayList<>();
            while (rs.next()) {
                SMSPreferencesEntity entity = new SMSPreferencesEntity();
                entity.setId(rs.getLong(1));
                entity.setInvoiceDay(rs.getInt(2));
                entity.setSmsCreditsLimit(rs.getInt(3));
                entity.setSenderNr(rs.getString(4));
                entity.setUnlimitedMessaging(rs.getBoolean(5));
                entity.setDeviceIp(rs.getString(6));
                entity.setDevicePort(rs.getString(7));
                entity.setGateway(rs.getBoolean(8));
                entity.setAuthKey(rs.getString(9));
                entity.setSmsLeft(rs.getInt(10));
                smsPref.add(entity);
            }

            if (!smsPref.isEmpty() && smsPref.size() >= 1) {
                preferencesEntity = smsPref.get(0);
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the SMS preferences: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return preferencesEntity;
    }

    public Long saveSms(SmsEntity sms) {
        String query = "INSERT INTO sms(type, sender, receiver, body, status, status_msg, sent_on) VALUES(?, ?, ?, ?, ?, ?, ?)";
        Long smsId = 0L;
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setShort(1, Constants.SMS_TYPE.valueOfLabel(sms.getType()).getType());
            pst.setString(2, sms.getSender());
            pst.setString(3, sms.getReceiver());
            pst.setString(4, sms.getBody());
            pst.setShort(5, Constants.SMS_STATUS.valueOfLabel(sms.getStatus()).getType());
            pst.setString(6, sms.getStatusMessage());
            pst.setDate(7, new java.sql.Date(sms.getSentOn().getTime()));
            
            pst.executeUpdate();
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                smsId = generatedKeys.getLong(1);
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        } catch (SQLException ex) {
            LOGGER.error("Exception saving the sms: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
       return smsId;
    }
    
    public void updateSMSStatus(short smsStatus, String smsMgs, Long id) {
        String query = "UPDATE sms set status = ?, status_msg = ? where id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                 pst.setShort(1, smsStatus);
                 pst.setString(2, smsMgs);
                 pst.setLong(3, id);
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("SMS updated successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception updating the sms: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }

    public void saveSmsPref(SMSPreferencesEntity smsPreferencesEntity) {
        String query = "INSERT INTO sms_pref(invoice_day, credits_limit, sender, unlimited, device_ip, device_port, gateway, auth_key, sms_left) VALUES(?, ?, ?, ?, ?, ?, ?, ?,?)";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, smsPreferencesEntity.getInvoiceDay());
            pst.setInt(2, smsPreferencesEntity.getSmsCreditsLimit());
            pst.setString(3, smsPreferencesEntity.getSenderNr());
            pst.setBoolean(4, smsPreferencesEntity.isUnlimitedMessaging());
            pst.setString(5, smsPreferencesEntity.getDeviceIp());
            pst.setString(6, smsPreferencesEntity.getDevicePort());
            pst.setBoolean(7, smsPreferencesEntity.isGateway());
            pst.setString(8, smsPreferencesEntity.getAuthKey());
            pst.setInt(9, smsPreferencesEntity.getSmsLeft());
            
            pst.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error("Exception saving the sms preferences: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }

    public void updateSmsPref(SMSPreferencesEntity preferencesEntity) {
        String query = "UPDATE sms_pref set invoice_day = ?, credits_limit = ?, sender = ?, unlimited = ?, device_ip = ?, device_port = ?, gateway = ?, auth_key = ?, sms_left = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setInt(1, preferencesEntity.getInvoiceDay());
                pst.setInt(2, preferencesEntity.getSmsCreditsLimit());
                pst.setString(3, preferencesEntity.getSenderNr());
                pst.setBoolean(4, preferencesEntity.isUnlimitedMessaging());
                pst.setString(5, preferencesEntity.getDeviceIp());
                pst.setString(6, preferencesEntity.getDevicePort());
                pst.setBoolean(7, preferencesEntity.isGateway());
                pst.setString(8, preferencesEntity.getAuthKey());
                pst.setInt(9, preferencesEntity.getSmsLeft());
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("SMS preferences updated successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception updating the sms preferences: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    
    public void updateCredits(int invoiceDay, int creditsLimit, boolean unlimited, int smsLeft) {
        String query = "UPDATE sms_pref set invoice_day = ?, credits_limit = ?, unlimited = ?, sms_left = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setInt(1, invoiceDay);
                pst.setInt(2, creditsLimit);
                pst.setBoolean(3, unlimited);
                pst.setInt(4, smsLeft);
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("SMS preferences updated successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception updating the credits: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
        
    public void updateSMSLeft(int smsLeft) {
        String query = "UPDATE sms_pref set sms_left = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setInt(1, smsLeft);
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("SMS preferences updated successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception updating the sms left: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
      public void saveAuthKey(String authKey) {
        String query = "UPDATE sms_pref set auth_key = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, authKey);
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("SMS preferences updated successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception saving the sms preferences: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }

    public void deleteSMS(Long id) {
        String query = "DELETE FROM sms WHERE id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setLong(1, id);
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("SMS deleted successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception deleting the sms with id: " + id + " and exception: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }

    public String getSenderNo() {
        String senderNo = "";
        String selectStatusSql = "SELECT sender FROM sms_pref";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement st = con.prepareStatement(selectStatusSql)) {
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        senderNo = rs.getString(1);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the sender number. Excepion: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return senderNo;
    }
    
    private Map<String, String> getDBConnection(){
        Preferences preferences = Preferences.userRoot().node(DatabaseSettingsController.class.getName());
        String dbURL = "jdbc:postgresql://localhost/msgPortal";
        String user = "postgres";
        String password = "admin";
        
        if(preferences != null){
         if(preferences.get(Utils.DB_URL_PREF, "jdbc:postgresql://localhost/msgPortal") != null){
                dbURL = preferences.get(Utils.DB_URL_PREF, "jdbc:postgresql://localhost/msgPortal");
            }

            if(preferences.get(Utils.DB_USER_PREF, "postgres") != null){
                user = preferences.get(Utils.DB_USER_PREF, "postgres");
            }

            if(preferences.get(Utils.DB_PASS_PREF, "admin") != null){
                password = preferences.get(Utils.DB_PASS_PREF, (String) null);
            }
        }else{
             preferences.put(Utils.DB_URL_PREF, dbURL);
             preferences.put(Utils.DB_USER_PREF, user);
             preferences.put(Utils.DB_PASS_PREF, password);
        }    
        
        Map<String, String> dbSettings = new HashMap<>();
        dbSettings.put(Utils.DB_URL_PREF, dbURL);
        dbSettings.put(Utils.DB_USER_PREF, user);
        dbSettings.put(Utils.DB_PASS_PREF, password);
        return dbSettings;
    }
}
