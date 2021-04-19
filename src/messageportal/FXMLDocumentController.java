package messageportal;

import messageportal.utils.ToastMessage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import messageportal.entities.SMSPreferencesEntity;
import messageportal.entities.SmsEntity;
import messageportal.services.SMSService;

public class FXMLDocumentController extends ListView<SmsEntity> implements Initializable {

    private ObservableList<SmsEntity> smsList;
    private static final SMSService smsService = new SMSService();
    private ObservableList<String> listTypeSelectOptions = FXCollections.observableArrayList("Sent", "Failed", "All", "Twilio", "Gateway");
    private SMSPreferencesEntity savedPref;
    private String selectedListType = "All"; //default value

    @FXML
    private JFXButton new_sms, settings, help, uriInfo, refreshBtn, dbSettings;

    @FXML
    private JFXListView<SmsEntity> smsListView;

    @FXML
    private JFXComboBox listSelect;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        new_sms.setGraphic(new ImageView(new Image("/messageportal/images/sms.png")));
        settings.setGraphic(new ImageView(new Image("/messageportal/images/settings.png")));
        help.setGraphic(new ImageView(new Image("/messageportal/images/help.png")));
        dbSettings.setGraphic(new ImageView(new Image("/messageportal/images/db.png")));
        uriInfo.setGraphic(new ImageView(new Image("/messageportal/images/uriInfo.png")));
        refreshBtn.setGraphic(new ImageView(new Image("/messageportal/images/refresh.png")));
        
        Preferences preferences = Preferences.userNodeForPackage(DatabaseSettingsController.class); 
        if(preferences != null){
                smsService.createDBSchema(); //create DB schema if it doesn't exist
                savedPref = smsService.getSavedPreferences();
        } else{
            ToastMessage.makeText("Please add the database settings.");
        }

        if(savedPref != null && !savedPref.isGateway())
            uriInfo.setVisible(false);
        else
            uriInfo.setVisible(true);

        List<SmsEntity> smsEntityList = FXCollections.observableArrayList(smsService.getSMSList(selectedListType));

        if (smsEntityList == null || smsEntityList.isEmpty()) {
            ToastMessage.makeText("No SMS sent yet.");
        }

        smsList = FXCollections.observableArrayList(smsEntityList);
        smsListView.setItems(smsList);
        smsListView.setCellFactory((ListView<SmsEntity> studentListView) -> new SMSListCell());
        smsListView.setOnMouseClicked((MouseEvent event) -> {
            SmsEntity selectedSMS = (SmsEntity) smsListView.getSelectionModel().getSelectedItem();
            ConfirmController confirmDialog = new ConfirmController(selectedSMS);
            boolean isConfirmed = confirmDialog.showAndWait();
            if(isConfirmed){
                ToastMessage.makeText("SMS deleted.");
                smsList = FXCollections.observableArrayList(smsService.getSMSList(selectedListType));
                smsListView.setItems(null);
                smsListView.setItems(smsList);
                smsListView.refresh();
            }
        });

        new_sms.setOnMouseClicked((MouseEvent event) -> {
           NewSMSController newSMS = new NewSMSController();
           boolean isSent = newSMS.showAndWait();
           if(isSent){
               if(selectedListType == null)
                    selectedListType = "All";
                smsList = FXCollections.observableArrayList(smsService.getSMSList(selectedListType));
                smsListView.setItems(null);
                smsListView.setItems(smsList);
                smsListView.refresh();
           }
        });

        settings.setOnMouseClicked((MouseEvent event) -> {
            new SettingsController();
        });

        help.setOnMouseClicked((MouseEvent event) -> {
            new HelpController();
        });

        dbSettings.setOnMouseClicked((MouseEvent event) -> {
           DatabaseSettingsController dbSettingsController = new DatabaseSettingsController();
           boolean isSettingsSaved = dbSettingsController.showAndWait();
           if(isSettingsSaved){
                smsService.createDBSchema(); //create DB schema if it doesn't exist and the settings have just been set for the first time
                savedPref = smsService.getSavedPreferences(); //refresh general settings
                smsList = FXCollections.observableArrayList(smsService.getSMSList(selectedListType)); //add list
                smsListView.setItems(null);
                smsListView.setItems(smsList);
                smsListView.refresh();
           }
        });


        uriInfo.setOnMouseClicked((MouseEvent event) -> {
            new URIInfoController();
        });

        refreshBtn.setOnMouseClicked((MouseEvent event) -> {
            smsList = FXCollections.observableArrayList(smsService.getSMSList(selectedListType));
            smsListView.setItems(null);
            smsListView.setItems(smsList);
            smsListView.refresh();
        });

        listSelect.setValue(selectedListType);
        listSelect.setItems(listTypeSelectOptions);

        listSelect.getSelectionModel().selectedItemProperty()
           .addListener(new ChangeListener<String>() {
               public void changed(ObservableValue<? extends String> observable,
                                   String oldValue, String newValue) {

                    selectedListType = newValue;
                    List<SmsEntity> selectedList = smsService.getSMSList(newValue);
                    smsList = FXCollections.observableArrayList(selectedList);
                    smsListView.setItems(smsList);
                    smsListView.setCellFactory((ListView<SmsEntity> studentListView) -> new SMSListCell());
               }
        });
    }
}
