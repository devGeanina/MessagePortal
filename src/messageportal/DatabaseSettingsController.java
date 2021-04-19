
package messageportal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import messageportal.utils.ToastMessage;
import messageportal.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseSettingsController {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseSettingsController.class);
    private Preferences prefs;

    @FXML
    private JFXTextField userTxt, passTxt;
    
    @FXML
    private JFXTextArea dbURLTxt;
    
    @FXML
    private JFXButton settingsCloseBtn, saveSettingsBtn;
    
    private FXMLLoader fxmlLoader;
    private Stage settingsStage;
    private boolean settingsComplete = false;
    
    public DatabaseSettingsController() {
        Parent parent = null;

        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/messageportal/fxml/DatabaseSettingsDialog.fxml"));
            fxmlLoader.setController(this);

            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the database settings dialog: " + e.getMessage());
            }
        }

        settingsCloseBtn.setGraphic(new ImageView(new Image("/messageportal/images/closeDialog.png")));
        settingsCloseBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageDetails = (Stage) settingsCloseBtn.getScene().getWindow();
            stageDetails.close();
        });
        
        prefs = Preferences.userRoot().node(this.getClass().getName());
        
        if(prefs != null){
            if(prefs.get(Utils.DB_URL_PREF, "jdbc:postgresql://localhost/msgPortal") != null){
                dbURLTxt.setText(prefs.get(Utils.DB_URL_PREF, "jdbc:postgresql://localhost/msgPortal"));
            }

            if(prefs.get(Utils.DB_USER_PREF, "postgres") != null){
                userTxt.setText(prefs.get(Utils.DB_USER_PREF, "postgres"));
            }

            if(prefs.get(Utils.DB_PASS_PREF, "admin") != null){
                passTxt.setText(prefs.get(Utils.DB_PASS_PREF, (String) null));
            }
        }

        saveSettingsBtn.setGraphic(new ImageView(new Image("/messageportal/images/save.png")));
        saveSettingsBtn.setOnMouseClicked((MouseEvent event) -> {
            
               // now set the values
                prefs.remove(Utils.DB_URL_PREF);
                prefs.remove(Utils.DB_USER_PREF);
                prefs.remove(Utils.DB_PASS_PREF);
               
                prefs.put(Utils.DB_URL_PREF, dbURLTxt.getText());
                prefs.put(Utils.DB_USER_PREF, userTxt.getText());
                prefs.put(Utils.DB_PASS_PREF, passTxt.getText());
                
                settingsComplete = true;
            
                //hide dialog
                Stage stageSettings = (Stage) saveSettingsBtn.getScene().getWindow();
                ToastMessage.makeText("Database settings saved.");
                stageSettings.close();
        });

        dbURLTxt.setStyle("-fx-text-inner-color: white");
        userTxt.setStyle("-fx-text-inner-color: white");
        passTxt.setStyle("-fx-text-inner-color: white");

        settingsStage = new Stage();
        settingsStage.initStyle(StageStyle.TRANSPARENT);
        settingsStage.setScene(new Scene(parent));
    }
     
       public boolean showAndWait() {
        settingsStage.showAndWait();
        return settingsComplete;
    }
}
