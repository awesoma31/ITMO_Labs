package org.awesoma.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.client.controllers.AuthController;
import org.awesoma.common.Environment;

import java.io.IOException;

public class App extends Application {
    public static final int SCREEN_WIDTH = 640;
    public static final int SCREEN_HEIGHT = 400;
    private static final Logger logger = LogManager.getLogger(App.class);
    private final FXMLLoader fxmlLoader = new FXMLLoader();
    private Client client;


    @Override
    public void start(Stage stage) throws IOException {

        var loginFxml = App.class.getResource("login-view.fxml");
        fxmlLoader.setLocation(loginFxml);
        Scene loginScene = new Scene(fxmlLoader.load(), SCREEN_WIDTH, SCREEN_HEIGHT);

        var loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        loader.load();
        AuthController authController = loader.getController();
//        authController.setClient(client);

        stage.setTitle("lab8");
        stage.setScene(loginScene);
        stage.centerOnScreen();
        stage.show();

//        client = new Client(Environment.HOST, Environment.PORT);
//        client.openSocket();

    }

    public static void main(String[] args) {
        logger.info("launching app");

        launch(args);
    }
}