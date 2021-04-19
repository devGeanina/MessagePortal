package messageportal;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import messageportal.entities.SmsEntity;
import messageportal.services.SMSService;
import messageportal.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SMSListCell extends ListCell<SmsEntity> {

    private static final Logger LOGGER = LogManager.getLogger(SMSListCell.class);
    private static final SMSService smsService = new SMSService();

    @FXML
    private Label receiver, sentTypeLbl;

    @FXML
    private Label body;

    @FXML
    private ImageView smsIcon;
    
    @FXML
    private JFXButton smsDetailBtn;

    @FXML
    private GridPane gridPane;

    private FXMLLoader fxmlLoader;

    @Override
    protected void updateItem(SmsEntity sms, boolean empty) {
        super.updateItem(sms, empty);

        if (empty || sms == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/messageportal/fxml/SMSListCell.fxml"));
                fxmlLoader.setController(this);

                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    LOGGER.error("Exception creating the list cell: " + e.getMessage());
                }
            }
            
            receiver.setMinWidth(Region.USE_PREF_SIZE);
            receiver.setText(sms.getReceiver());
            body.setText(sms.getBody());
            
            smsDetailBtn.setGraphic(new ImageView(new Image("/messageportal/images/detail.png")));
            smsDetailBtn.setOnMouseClicked((MouseEvent event) -> {
                    new SMSDetailsController(sms);
            });
            
            if(sms.getType().equalsIgnoreCase(Constants.SMS_TYPE.TWILIO.getName())){
                sentTypeLbl.setText("T");
                sentTypeLbl.setTextFill(Color.web("#e74c3c", 0.8));
            }else{
                sentTypeLbl.setText("G");
                sentTypeLbl.setTextFill(Color.web("#00b894", 0.8));
            }
            
            if (sms.getStatus().equalsIgnoreCase(Constants.SMS_STATUS.SENT.getName())) {
                smsIcon.setImage(new Image("/messageportal/images/sent.png"));
            } else if (sms.getStatus().equalsIgnoreCase(Constants.SMS_STATUS.ERROR.getName())) {
                smsIcon.setImage(new Image("/messageportal/images/error.png"));
            } else {
                smsIcon.setImage(new Image("/messageportal/images/unknown.png"));
            }

            setText(null);
            setGraphic(gridPane);
            gridPane.setCache(true);
        }
    }
}
