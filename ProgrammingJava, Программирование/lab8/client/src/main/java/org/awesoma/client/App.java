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
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);
    private static Client client;
    private Stage mainStage;

    private AuthController authController;
//    private EditController editController;
    private MainController mainController;
    private Localizator localizator;

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
//        initControllers();

        authStage();
    }

    public void authStage() {
        var authLoader = new FXMLLoader(getClass().getResource("fxml/login-view.fxml"));
        var authRoot = loadFxml(authLoader);
        AuthController authController = authLoader.getController();

        authController.setCallback(this::mainStage);
        authController.setClient(client);
        authController.setLocalizator(localizator);

        mainStage.setScene(new Scene(authRoot));
        mainStage.setTitle("Products");
        mainStage.setResizable(true);
        mainStage.show();
    }

    public void mainStage() {
        var mainLoader = new FXMLLoader(getClass().getResource("fxml/main-view.fxml"));
        var mainRoot = loadFxml(mainLoader);
        mainController = mainLoader.getController();

        var authLoader = new FXMLLoader(getClass().getResource("fxml/login-view.fxml"));
        var authRoot = loadFxml(authLoader);
        authController = authLoader.getController();

        var editLoader = new FXMLLoader(getClass().getResource("fxml/edit-view.fxml"));
        var editRoot = loadFxml(editLoader);
        EditController editController = editLoader.getController();

        mainController.setLocalizator(localizator);
        mainController.setClient(client);
        mainController.setAuthCallback(this::authStage);
        mainController.setEditCallback(this::editStage);
        mainController.fillTable();
        mainController.setEditController(editController);
        mainController.setEditCallback(this::editStage);

        editController.setMainController(mainController);
//        editController.setRoot(editRoot);

        mainController.fillTable();

        mainStage.setScene(new Scene(mainRoot));
        mainStage.centerOnScreen();
        mainStage.show();
    }

    public void editStage() {
        var editLoader = new FXMLLoader(getClass().getResource("fxml/edit-view.fxml"));
        var editRoot = loadFxml(editLoader);
        EditController controller = editLoader.getController();
        controller.setLocalizator(localizator);
        controller.setClient(client);
        controller.setMainController(mainController);

        Stage stage = new Stage();
        Scene scene = new Scene(editRoot);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.showAndWait();
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