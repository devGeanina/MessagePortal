package messageportal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.util.function.UnaryOperator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import messageportal.entities.SMSPreferencesEntity;
import messageportal.services.SMSService;
import messageportal.utils.ToastMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettingsController {

    private static final Logger LOGGER = LogManager.getLogger(SettingsController.class);
    private static final SMSService smsService = new SMSService();

    @FXML
    private JFXTextField senderNr, deviceIP, devicePort;

    @FXML
    private JFXButton settingsCloseBtn, saveSettingsBtn;

    @FXML
    private Spinner creditsLimit, invoiceDay;

    @FXML
    private JFXCheckBox unlimited, gateway;

    private FXMLLoader fxmlLoader;

    public SettingsController() {
        Parent parent = null;

        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/messageportal/fxml/SettingsDialog.fxml"));
            fxmlLoader.setController(this);

            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the settings dialog: " + e.getMessage());
            }
        }

        final SMSPreferencesEntity savedPreferences = smsService.getSavedPreferences();

        int initialInvoiceDayValue = 1;
        int initialCreditsLimitValue = 100;
        if (savedPreferences != null) {
            initialInvoiceDayValue = savedPreferences.getInvoiceDay();
            initialCreditsLimitValue = savedPreferences.getSmsCreditsLimit();
            unlimited.setSelected(savedPreferences.isUnlimitedMessaging());
            senderNr.setText(savedPreferences.getSenderNr());
            if (savedPreferences.getDeviceIp() != null) {
                deviceIP.setText(savedPreferences.getDeviceIp());
            }
            if (savedPreferences.getDevicePort() != null) {
                devicePort.setText(savedPreferences.getDevicePort());
            }
            gateway.setSelected(savedPreferences.isGateway());
        }

        if (unlimited.isSelected()) {
            creditsLimit.setDisable(true);
        } else {
            creditsLimit.setDisable(false);
        }

        unlimited.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                unlimited.setSelected(true);
                creditsLimit.setDisable(true);
            } else {
                unlimited.setSelected(false);
                creditsLimit.setDisable(false);
            }
        });

        //Gateway is selected enable device ip and port and disable sender number, the Android SIM will be used by default
        //Gateway is not selected enable sender number and disable device ip and port as Twilio does not need the info
        //credit, unlimited and invoice day are retrieved from Android gateway if gateway is selected
        gateway.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                gateway.setSelected(true);
                deviceIP.setDisable(false);
                devicePort.setDisable(false);
                senderNr.setDisable(true);
                creditsLimit.setDisable(true);
                invoiceDay.setDisable(true);
                unlimited.setDisable(true);
            } else {
                gateway.setSelected(false);
                deviceIP.setDisable(true);
                devicePort.setDisable(true);
                creditsLimit.setDisable(false);
                invoiceDay.setDisable(false);
                unlimited.setDisable(false);
            }
        });
        
        // device port must be numeric
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        devicePort.setTextFormatter(textFormatter);

        invoiceDay.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, initialInvoiceDayValue, 1)); // 1-30 days of the month

        creditsLimit.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, initialCreditsLimitValue, 1));

        settingsCloseBtn.setGraphic(new ImageView(new Image("/messageportal/images/closeDialog.png")));
        settingsCloseBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageDetails = (Stage) settingsCloseBtn.getScene().getWindow();
            stageDetails.close();
        });

        saveSettingsBtn.setGraphic(new ImageView(new Image("/messageportal/images/save.png")));
        saveSettingsBtn.setOnMouseClicked((MouseEvent event) -> {

            SMSPreferencesEntity newPref;
            if (savedPreferences == null) {
                newPref = new SMSPreferencesEntity();
            } else {
                newPref = savedPreferences;
            }

            if (invoiceDay.getValue() != null) {
                newPref.setInvoiceDay((int) invoiceDay.getValue());
            }
            if (creditsLimit.getValue() != null) {
                newPref.setSmsCreditsLimit((int) creditsLimit.getValue());
            }
            if (senderNr.getText() != null) {
                newPref.setSenderNr(senderNr.getText().trim());
            }
            if (deviceIP.getText() != null) {
                newPref.setDeviceIp(deviceIP.getText().trim());
            }

            if (devicePort.getText() != null) {
                newPref.setDevicePort(devicePort.getText().trim());
            }

            newPref.setUnlimitedMessaging(unlimited.isSelected());
            if(gateway != null && gateway.isSelected())
                newPref.setGateway(true);
            else if (gateway != null && !gateway.isSelected())
               newPref.setGateway(false);
            smsService.saveSMSPref(newPref);

            //hide dialog
            Stage stageSettings = (Stage) saveSettingsBtn.getScene().getWindow();
            ToastMessage.makeText("Settings saved.");
            stageSettings.close();
        });

        senderNr.setStyle("-fx-text-inner-color: white");
        deviceIP.setStyle("-fx-text-inner-color: white");
        devicePort.setStyle("-fx-text-inner-color: white");

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(parent));
        stage.show();
    }
}
