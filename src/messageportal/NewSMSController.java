package messageportal;

import messageportal.utils.ToastMessage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.util.Date;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import messageportal.entities.SMSPreferencesEntity;
import messageportal.entities.SmsEntity;
import messageportal.services.SMSService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;


public class NewSMSController {

    private static final Logger LOGGER = LogManager.getLogger(NewSMSController.class);
    private static final SMSService smsService = new SMSService();
    private boolean isSent = false;

    @FXML
    private JFXTextField receiverNr;

    @FXML
    private JFXButton sendCloseBtn, saveBtn;

    @FXML
    private JFXTextArea bodyText;

    private FXMLLoader fxmlLoader;
    
    private Stage smsStage;

    public NewSMSController() {
        Parent parent = null;

        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/messageportal/fxml/NewSMSDialog.fxml"));
            fxmlLoader.setController(this);

            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the new sms dialog: " + e.getMessage());
            }
        }

        sendCloseBtn.setGraphic(new ImageView(new Image("/messageportal/images/closeDialog.png")));
        sendCloseBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageDetails = (Stage) sendCloseBtn.getScene().getWindow();
            stageDetails.close();
        });

        final SMSPreferencesEntity savedPreferences = smsService.getSavedPreferences();
        saveBtn.setGraphic(new ImageView(new Image("/messageportal/images/send.png")));
        saveBtn.setOnMouseClicked((MouseEvent event) -> {
            SmsEntity newSMS = new SmsEntity();
            newSMS.setReceiver(receiverNr.getText());
            newSMS.setBody(bodyText.getText()); 
            newSMS.setSentOn(new Date(System.currentTimeMillis()));
            if (savedPreferences != null) {
                newSMS.setSender(savedPreferences.getSenderNr());
                isSent = true;
                Service<Void> service = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {           
                            @Override
                            protected Void call() throws Exception {
                                //Background work                       
                                final CountDownLatch latch = new CountDownLatch(1);
                                Platform.runLater(() -> {
                                    try{
                                        boolean isSmsSent = smsService.sendSMS(newSMS, savedPreferences);
                                        if(isSmsSent){
                                            ToastMessage.makeText("SMS Sent.");
                                        }else
                                            ToastMessage.makeText("Failed sending SMS.");
                                    }finally{
                                        latch.countDown();
                                    }
                                });
                                latch.await();                      
                                return null;
                            }
                        };
                    }
                };
                service.start();
                
                //hide dialog
                Stage stageDetails = (Stage) saveBtn.getScene().getWindow();
                stageDetails.close();
            } else //show message to fill out settings
            {
                ToastMessage.makeText("Please fill in the application settings information first.");
            }
        });

        receiverNr.setStyle("-fx-text-inner-color: white");
        bodyText.setStyle("-fx-text-inner-color: white");

        smsStage = new Stage();
        smsStage.initStyle(StageStyle.TRANSPARENT);
        smsStage.setScene(new Scene(parent));
    }
    
    public boolean showAndWait() {
        smsStage.showAndWait();
        return isSent;
    }
}
