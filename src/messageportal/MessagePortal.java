package messageportal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MessagePortal extends Application {

    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws Exception {
        setPrimaryStage(stage);
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/messageportal/fxml/FXMLDocument.fxml"));
        Parent root = loader.load();
        stage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        MessagePortal.primaryStage = primaryStage;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
