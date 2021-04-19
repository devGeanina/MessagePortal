package messageportal.utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import messageportal.MessagePortal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ToastMessage {

    final static int TOAST_TIME = 3000; //3 seconds
    final static int FADE_IN_TIME = 300; //0.3 seconds
    final static int FADE_OUT_TIME = 300; //0.3 seconds
    private static final Logger LOGGER = LogManager.getLogger(ToastMessage.class);

    public static void makeText(String toastMsg) {
        Stage toastStage = new Stage();
        toastStage.initOwner(MessagePortal.getPrimaryStage());
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(toastMsg);
        text.setFont(Font.font("Times New Roman", 20));
        text.setFill(Color.WHITE);

        StackPane root = new StackPane(text);
        root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.3); -fx-padding: 50px;");
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);
        toastStage.show();

        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(FADE_IN_TIME), new KeyValue(toastStage.getScene().getRoot().opacityProperty(), 1));
        fadeInTimeline.getKeyFrames().add(fadeInKey1);
        fadeInTimeline.setOnFinished((ae)
                -> {
            new Thread(() -> {
                try {
                    Thread.sleep(TOAST_TIME);
                } catch (InterruptedException e) {
                    LOGGER.error("Exception showing the toast: " + e.getMessage());
                }
                Timeline fadeOutTimeline = new Timeline();
                KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(FADE_OUT_TIME), new KeyValue(toastStage.getScene().getRoot().opacityProperty(), 0));
                fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
                fadeOutTimeline.setOnFinished((aeb) -> toastStage.close());
                fadeOutTimeline.play();
            }).start();
        });
        fadeInTimeline.play();
    }
}
