package com.lrogerscs.marketlynx;

import com.lrogerscs.marketlynx.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main loads the default FXML file, sets window name/icon.
 *
 * @author Lee Rogers
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
        MainController controller = fxmlLoader.getController();

        stage.getIcons().add(new Image(getClass().getResource("image/marketLynxAppIcon.png").toExternalForm()));
        stage.setTitle("marketLynx");
        stage.setScene(scene);
        stage.show();

        // Call setDefault ONLY once stage has been shown to properly calculate positions.
        controller.setDefault();
    }

    public static void main(String[] args) {
        launch();
    }
}