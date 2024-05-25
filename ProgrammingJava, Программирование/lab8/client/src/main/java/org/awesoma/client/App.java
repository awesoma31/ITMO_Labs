package org.awesoma.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.client.controllers.AuthController;
import org.awesoma.client.controllers.EditController;
import org.awesoma.client.controllers.MainController;
import org.awesoma.client.util.Localizator;
import org.awesoma.common.Environment;

import java.io.IOException;
import java.util.*;

public class App extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);
    private static Client client;
    private Stage mainStage;
    private Localizator localizator;
    private AuthController authController;
    private Parent authRoot;

    public static void main(String[] args) {
        logger.info("launching app");

        client = new Client(Environment.HOST, Environment.PORT);
        client.openSocket();

        launch(args);
    }

    @Override
    public synchronized void start(Stage stage) {
        mainStage = stage;
        localizator = new Localizator(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("en")));

        authStage();
    }

    public void authStage() {
        var authLoader = new FXMLLoader(getClass().getResource("fxml/login-view.fxml"));
        authRoot = loadFxml(authLoader);
        authController = authLoader.getController();
        authController.setCallback(this::mainStage);
        authController.setClient(client);
        authController.setLocalizator(localizator);
        authController.changeLanguage();
        mainStage.setScene(new Scene(authRoot));
        mainStage.setTitle(localizator.getKeyString("lab8"));
        mainStage.setResizable(true);
        mainStage.setOnCloseRequest(e -> {
            System.exit(0);
        });
        mainStage.show();
    }

    public void mainStage() {
        var authLoader = new FXMLLoader(getClass().getResource("fxml/login-view.fxml"));
        authRoot = loadFxml(authLoader);
        authController = authLoader.getController();
        authController.setCallback(this::mainStage);
        authController.setClient(client);
        authController.setLocalizator(localizator);

        var editLoader = new FXMLLoader(getClass().getResource("fxml/edit-view.fxml"));
        loadFxml(editLoader);
        EditController editController = editLoader.getController();

        var mainLoader = new FXMLLoader(getClass().getResource("fxml/main-view.fxml"));
        Parent mainRoot = loadFxml(mainLoader);
        MainController mainController = mainLoader.getController();

        mainController.setLocalizator(localizator);
        mainController.setClient(client);
        mainController.setAuthCallback(this::authStage);
        mainController.fillTable();
        mainController.setEditController(editController);;

        editController.setMainController(mainController);

        mainController.fillTable();
        mainController.changeLanguage();

        mainStage.setScene(new Scene(mainRoot));
        mainStage.centerOnScreen();
        mainStage.show();
    }

    private Parent loadFxml(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            logger.error("Can't load " + loader, e);
            System.exit(1);
        }
        return null;
    }
}