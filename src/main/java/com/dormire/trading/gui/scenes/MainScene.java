package com.dormire.trading.gui.scenes;

import com.dormire.trading.gui.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainScene extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/main.fxml"));

        MainController controller = new MainController();
        loader.setController(controller);

        Parent root = loader.load();

        Scene scene = new Scene(root, Color.BLACK);
        controller.setMainScene(scene);

        stage.setScene(scene);
        stage.setTitle("Stonk GUI v0.5");
        stage.setMaximized(true);
        stage.setOnCloseRequest(e -> controller.shutdown());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}