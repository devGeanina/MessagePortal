package messageportal;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import messageportal.entities.SmsEntity;
import messageportal.utils.Constants;
import messageportal.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SMSDetailsController {

    private static final Logger LOGGER = LogManager.getLogger(SMSListCell.class);

    @FXML
    private Label fromText, receivedText, smsBody;

    @FXML
    private JFXButton closeBtn;

    private FXMLLoader fxmlLoader;

    public SMSDetailsController(SmsEntity selectedSMS) {
        Parent parent = null;

        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/messageportal/fxml/SMSDetails.fxml"));
            fxmlLoader.setController(this);

            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the list cell: " + e.getMessage());
            }
        }

        fromText.setMinWidth(Region.USE_PREF_SIZE);
        fromText.setText(selectedSMS.getSender());
        receivedText.setMinWidth(Region.USE_PREF_SIZE);
        receivedText.setText(Utils.getStringFromDate(selectedSMS.getSentOn()));
        
        if(selectedSMS.getStatusMessage() != null && selectedSMS.getStatus().equalsIgnoreCase(Constants.SMS_STATUS.ERROR.getName()))
            smsBody.setText(selectedSMS.getBody().concat(" - Error: ").concat(selectedSMS.getStatusMessage()));
        else if(selectedSMS.getStatus().equalsIgnoreCase(Constants.SMS_STATUS.PENDING.getName()))
            smsBody.setText(selectedSMS.getBody().concat(" - Error: SMS status is PENDING, server didn't communicate new status in time or is still processing."));
        else
            smsBody.setText(selectedSMS.getBody());

        smsBody.setWrapText(true);
        smsBody.setPrefHeight(150);
        smsBody.setPrefWidth(298);

        closeBtn.setGraphic(new ImageView(new Image("/messageportal/images/closeDialog.png")));

        closeBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageDetails = (Stage) closeBtn.getScene().getWindow();
            stageDetails.close();
        });

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(parent));
        stage.show();
    }
}
