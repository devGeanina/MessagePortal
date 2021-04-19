/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messageportal.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import messageportal.entities.SMSPreferencesEntity;
import messageportal.entities.SmsEntity;
import messageportal.utils.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class) 
public class SMSServiceTest {
    
    public SMSServiceTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    
    @Mock
    DatabaseService databaseServiceMock;
    
    private List<SmsEntity> testSMSEntities(){
       List<SmsEntity> smsEntities = new ArrayList<>();
       smsEntities.add(new SmsEntity("+342424425", "+4435345355", "Test SMS Gateway 1", 1L, Constants.SMS_STATUS.SENT.getName(), "Message sent", new Date(), Constants.SMS_TYPE.GATEWAY.getName()));
       smsEntities.add(new SmsEntity("+464564646", "+7686786865", "Test SMS Gateway 2", 2L, Constants.SMS_STATUS.ERROR.getName(), "Message failed", new Date(), Constants.SMS_TYPE.GATEWAY.getName()));
       smsEntities.add(new SmsEntity("+342424425", "+4564564644", "Test SMS Gateway 3", 3L, Constants.SMS_STATUS.SENT.getName(), "Message sent", new Date(), Constants.SMS_TYPE.GATEWAY.getName()));
       smsEntities.add(new SmsEntity("+464564646", "+545645646", "Test SMS Twilio 1", 4L, Constants.SMS_STATUS.SENT.getName(), "Message sent", new Date(), Constants.SMS_TYPE.TWILIO.getName()));
       smsEntities.add(new SmsEntity("+464564646", "+545645646", "Test SMS Twilio 2", 4L, Constants.SMS_STATUS.ERROR.getName(), "Message failed", new Date(), Constants.SMS_TYPE.TWILIO.getName()));
       return smsEntities;
    }
    
    private SMSPreferencesEntity getLimitedSMSPref(){
        SMSPreferencesEntity smsPrefEntity = new SMSPreferencesEntity();
        smsPrefEntity.setAuthKey("@423B342b2f");
        smsPrefEntity.setDeviceIp("192.22.100.1");
        smsPrefEntity.setDevicePort("9090");
        smsPrefEntity.setGateway(true);
        smsPrefEntity.setId(1L);
        smsPrefEntity.setInvoiceDay(1);
        smsPrefEntity.setSenderNr("+464564646");
        smsPrefEntity.setSmsCreditsLimit(2);
        smsPrefEntity.setSmsLeft(1);
        smsPrefEntity.setUnlimitedMessaging(false);
        return smsPrefEntity;
    }
    
     private SMSPreferencesEntity getUnLimitedSMSPref(){
        SMSPreferencesEntity smsPrefEntity = new SMSPreferencesEntity();
        smsPrefEntity.setAuthKey("@423B342b2f");
        smsPrefEntity.setDeviceIp("192.22.100.1");
        smsPrefEntity.setDevicePort("9090");
        smsPrefEntity.setGateway(true);
        smsPrefEntity.setId(1L);
        smsPrefEntity.setInvoiceDay(1);
        smsPrefEntity.setSenderNr("+464564646");
        smsPrefEntity.setSmsCreditsLimit(2);
        smsPrefEntity.setSmsLeft(1);
        smsPrefEntity.setUnlimitedMessaging(true);
        return smsPrefEntity;
    }
    
    @Test
    public void testUnlimitedCredits()  {
        assertNotNull(databaseServiceMock);
        when(databaseServiceMock.getAllSmsList()).thenReturn(testSMSEntities());  
        SMSService smsService  = new SMSService(databaseServiceMock);        
        when(databaseServiceMock.getSmsPreferences()).thenReturn(getUnLimitedSMSPref());
        boolean isCreditsUnlimited = smsService.isCreditsUnlimited();
        assertTrue(isCreditsUnlimited);
        boolean isLimitSurpassed = smsService.isSMSLimitSurpassed();
        Assertions.assertFalse(isLimitSurpassed);
    }
    
     @Test
    public void testLimitedCredits()  {
        assertNotNull(databaseServiceMock);
        SMSService smsService  = new SMSService(databaseServiceMock);        
        when(databaseServiceMock.getSmsPreferences()).thenReturn(getLimitedSMSPref());
        boolean isCreditsUnlimited = smsService.isCreditsUnlimited();
        Assertions.assertFalse(isCreditsUnlimited);
        
        boolean isLimitSurpassed = smsService.isSMSLimitSurpassed();
        Assertions.assertFalse(isLimitSurpassed);
        
        int smsLeft = smsService.smsMessagesLeftToSend();
        assertEquals(1,smsLeft);
    }
    

    @Test
    public void testSendTwilioSMS() {
        SmsEntity smsEntity = null;
        SMSPreferencesEntity sPreferencesEntity = null;
        SMSService instance = new SMSService();
        SmsEntity expResult = null;
        SmsEntity result = instance.sendTwilioSMS(smsEntity, sPreferencesEntity);
        assertEquals(expResult, result);
    }

    @Test
    public void testSendGatewaySMS() {
        SmsEntity smsEntity = null;
        SMSPreferencesEntity savedPreferences = null;
        SMSService instance = new SMSService();
        SmsEntity expResult = null;
        SmsEntity result = instance.sendGatewaySMS(smsEntity, savedPreferences);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsSMSSent() {
        String id = "1";
        when(databaseServiceMock.getSMSStatusById(id)).thenReturn(Constants.SMS_STATUS.SENT.getName());
        SMSService instance = new SMSService();
        boolean expResult = true;
        boolean result = instance.isSMSSent(id);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetSmsStatusMessageFromDB() {
        String id = "1";
        when(databaseServiceMock.getSMSStatusMessageById(id)).thenReturn("test");
        SMSService instance = new SMSService();
        String expResult = "test";
        String result = instance.getSmsStatusMessageFromDB(id);
        assertEquals(expResult, result);
    }


    @Test
    public void testGetSMSStatusFromDB() {
      String id = "1";
        when(databaseServiceMock.getSMSStatusById(id)).thenReturn(Constants.SMS_STATUS.ERROR.getName());
        SMSService instance = new SMSService();
        String expResult = Constants.SMS_STATUS.ERROR.getName();
        String result = instance.getSMSStatusFromDB(id);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetAllSMSList() {
        SMSService instance = new SMSService();
        when(databaseServiceMock.getAllSmsList()).thenReturn(testSMSEntities());
        List<SmsEntity> expResult = testSMSEntities();
        List<SmsEntity> result = instance.getAllSMSList();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetSentSMSList() {
        List<SmsEntity> sentSMS = new ArrayList<>();
        List<SmsEntity> allSMS = testSMSEntities();
        allSMS.stream().filter(smsEntity -> (smsEntity.getStatus().equalsIgnoreCase(Constants.SMS_STATUS.SENT.getName()))).forEachOrdered(smsEntity -> {
            sentSMS.add(smsEntity);
        });
        when(databaseServiceMock.getSentSmsList()).thenReturn(sentSMS);
        SMSService instance = new SMSService();
        List<SmsEntity> expResult = sentSMS;
        List<SmsEntity> result = instance.getSentSMSList();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetErrorSMSList() {
        List<SmsEntity> allSMS = testSMSEntities();
        List<SmsEntity> errorSMS = allSMS.stream()
        .filter(x->x.getStatus().equalsIgnoreCase(Constants.SMS_STATUS.ERROR.getName()))
        .collect(Collectors.toList());
        SMSService instance = new SMSService();
        when(databaseServiceMock.getErrorSmsList()).thenReturn(errorSMS);
        List<SmsEntity> expResult = errorSMS;
        List<SmsEntity> result = instance.getErrorSMSList();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetGatewaySMSList() {
        List<SmsEntity> allSMS = testSMSEntities();
        List<SmsEntity> gatewaySMS = allSMS.stream()
        .filter(x->x.getStatus().equalsIgnoreCase(Constants.SMS_TYPE.GATEWAY.getName()))
        .collect(Collectors.toList());
        SMSService instance = new SMSService();
        when(databaseServiceMock.getErrorSmsList()).thenReturn(gatewaySMS);
        List<SmsEntity> expResult = gatewaySMS;
        List<SmsEntity> result = instance.getGatewaySMSList();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetTwilioSMSList() {
        List<SmsEntity> allSMS = testSMSEntities();
        List<SmsEntity> twilioSMS = allSMS.stream()
        .filter(x->x.getStatus().equalsIgnoreCase(Constants.SMS_TYPE.TWILIO.getName()))
        .collect(Collectors.toList());
        SMSService instance = new SMSService();
        when(databaseServiceMock.getTwilioSmsList()).thenReturn(twilioSMS);
        List<SmsEntity> expResult = twilioSMS;
        List<SmsEntity> result = instance.getTwilioSMSList();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetSMSList() {
        String selectionType = "Failed";
        List<SmsEntity> allSMS = testSMSEntities();
        List<SmsEntity> failedSMS = allSMS.stream()
        .filter(x->x.getStatus().equalsIgnoreCase(Constants.SMS_STATUS.ERROR.getName()))
        .collect(Collectors.toList());
        SMSService instance = new SMSService();
        when(databaseServiceMock.getErrorSmsList()).thenReturn(failedSMS);
        List<SmsEntity> expResult = failedSMS;
        List<SmsEntity> result = instance.getSMSList(selectionType);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetSavedPreferences() {
        when(databaseServiceMock.getSmsPreferences()).thenReturn(getLimitedSMSPref());
        SMSService instance = new SMSService();
        SMSPreferencesEntity expResult = getLimitedSMSPref();
        SMSPreferencesEntity result = instance.getSavedPreferences();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetSenderNo() {
        when(databaseServiceMock.getSenderNo()).thenReturn("2123123123");
        SMSService instance = new SMSService();
        String expResult = "2123123123";
        String result = instance.getSenderNo();
        assertEquals(expResult, result);
    }
}
