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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import messageportal.entities.SMSPreferencesEntity;
import messageportal.services.SMSService;
import messageportal.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelpController {
    
    private static final Logger LOGGER = LogManager.getLogger(HelpController.class);
    private static final SMSService smsService = new SMSService();

    
    @FXML
    private JFXButton helpCloseBtn;

    @FXML
    private Label versionTxt, phoneNoTxt, smsLeftTxt;
    
    private FXMLLoader fxmlLoader;
    
    public HelpController() {
        Parent parent = null;
        
        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/messageportal/fxml/HelpDialog.fxml"));
            fxmlLoader.setController(this);
            
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the help dialog: " + e.getMessage());
            }
        }
        
        SMSPreferencesEntity sPreferencesEntity = smsService.getSavedPreferences();
        if(sPreferencesEntity != null && sPreferencesEntity.getSmsLeft() != 0)
            smsLeftTxt.setText(String.valueOf(sPreferencesEntity.getSmsLeft()));
        else if(sPreferencesEntity != null && sPreferencesEntity.getSmsCreditsLimit() != 0)
            smsLeftTxt.setText(String.valueOf(sPreferencesEntity.getSmsCreditsLimit()));
        
        phoneNoTxt.setWrapText(true);
        phoneNoTxt.setTextAlignment(TextAlignment.JUSTIFY);
        phoneNoTxt.setMaxWidth(166); 
        phoneNoTxt.setMinHeight(Region.USE_PREF_SIZE);
        
        versionTxt.setText(Utils.APP_VERSION);
        
        helpCloseBtn.setGraphic(new ImageView(new Image("/messageportal/images/closeDialog.png")));
        helpCloseBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageDetails = (Stage) helpCloseBtn.getScene().getWindow();
            stageDetails.close();
        });
        
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(parent));
        stage.show();
    }
}
