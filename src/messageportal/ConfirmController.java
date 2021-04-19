
package messageportal;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import messageportal.entities.SmsEntity;
import messageportal.services.SMSService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfirmController {
    private static final Logger LOGGER = LogManager.getLogger(HelpController.class);
    private static final SMSService smsService = new SMSService();
    private boolean isConfirmed = false;
    private Stage confirmStage;
    
    @FXML
    private JFXButton noBtn, yesBtn;

    private FXMLLoader fxmlLoader;
    
    public ConfirmController(SmsEntity smsEntity) {
        Parent parent = null;
        
        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/messageportal/fxml/ConfirmDialog.fxml"));
            fxmlLoader.setController(this);
            
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the help dialog: " + e.getMessage());
            }
        }
        
        noBtn.setText("No");
        yesBtn.setText("Yes");
        noBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageDetails = (Stage) noBtn.getScene().getWindow();
            stageDetails.close();
        });
        
        yesBtn.setOnMouseClicked((MouseEvent event) -> {
            smsService.deleteSMS(smsEntity.getId());
            isConfirmed = true;
            Stage stageDetails = (Stage) noBtn.getScene().getWindow();
            stageDetails.close();
        });
        
        confirmStage = new Stage();
        confirmStage.initStyle(StageStyle.TRANSPARENT);
        confirmStage.setScene(new Scene(parent));
    }
    
     public boolean showAndWait() {
        confirmStage.showAndWait();
        return isConfirmed;
    }
}
