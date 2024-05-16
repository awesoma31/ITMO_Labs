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
import org.awesoma.common.Environment;

import java.io.IOException;

public class App extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);
    private static Client client;
    private Stage mainStage;

    public static void main(String[] args) {
        logger.info("launching app");

        launch(args);
    }

    @Override
    public void init() throws Exception {
        client = new Client(Environment.HOST, Environment.PORT);
        client.openSocket();

        super.init();
    }

    @Override
    public void start(Stage stage) {
        mainStage = stage;

        authStage();
    }

    public void authStage() {
        var mainLoader = new FXMLLoader(getClass().getResource("fxml/main-view.fxml"));
        loadFxml(mainLoader);
        MainController mainController = mainLoader.getController();
        mainController.setClient(client);

        var authLoader = new FXMLLoader(getClass().getResource("fxml/login-view.fxml"));
        var authRoot = loadFxml(authLoader);
        AuthController authController = authLoader.getController();
        authController.setClient(client);
        authController.setCallback(this::mainStage);
        authController.setMainController(mainController);
        authController.changeLanguage();


        mainStage.setScene(new Scene(authRoot));
        mainStage.setTitle("lab8");
        mainStage.centerOnScreen();
        mainStage.setResizable(false);
        mainStage.show();
    }

    public void mainStage() {
        try {
            var mainLoader = new FXMLLoader(getClass().getResource("fxml/main-view.fxml"));
            var mainScene = new Scene(mainLoader.load());

            MainController mainController = mainLoader.getController();
            mainController.setClient(client);
            mainController.setAuthCallback(this::authStage);
            mainController.setEditCallback(this::editStage);
            mainController.fillTable();
            mainController.changeLanguage();

            mainStage.centerOnScreen();

            mainStage.setScene(mainScene);
            mainStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void editStage() {
        var editLoader = new FXMLLoader(getClass().getResource("fxml/edit-view.fxml"));
        var editRoot = loadFxml(editLoader);
        EditController editController = editLoader.getController();
        editController.setClient(client);
        editController.setMainStageCallback(this::mainStage);
        editController.changeLanguage();

        var mainLoader = new FXMLLoader(getClass().getResource("fxml/main-view.fxml"));
        var mainScene = new Scene(mainLoader.load());

        MainController mainController = mainLoader.getController();
        editController.setMainController(mainController);


        mainStage.setScene(new Scene(editRoot));
        mainStage.setTitle("lab8");
        mainStage.centerOnScreen();
        mainStage.setResizable(false);
    }

    private Parent loadFxml(FXMLLoader loader) {
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            logger.error("Can't load " + loader, e);
            System.exit(1);
        }
        return parent;
    }
}