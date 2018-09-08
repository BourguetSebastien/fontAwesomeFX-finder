package fr.cmdor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public class MainApp extends Application {
    public static void main( String[] args ) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/Viewer.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("FontAwesomeFX finder");
        primaryStage.show();
    }
}
