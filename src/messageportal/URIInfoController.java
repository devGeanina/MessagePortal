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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import messageportal.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class URIInfoController {
    private static final Logger LOGGER = LogManager.getLogger(URIInfoController.class);
    
    @FXML
    private JFXButton uriCloseBtn;

    @FXML
    private Label uriSendTxt, uriCreditTxt;
    
    private FXMLLoader fxmlLoader;
    
     public URIInfoController() {
        Parent parent = null;
        
        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/messageportal/fxml/URIInfoDialog.fxml"));
            fxmlLoader.setController(this);
            
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the uri info dialog: " + e.getMessage());
            }
        }
        
        uriSendTxt.setText(String.join(",", Utils.SEND_PARAMS));
        uriCreditTxt.setText(String.join(",", Utils.CREDIT_PARAMS));
        
        uriCloseBtn.setGraphic(new ImageView(new Image("/messageportal/images/closeDialog.png")));
        uriCloseBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageDetails = (Stage) uriCloseBtn.getScene().getWindow();
            stageDetails.close();
        });
        
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(parent));
        stage.show();
    }
}
