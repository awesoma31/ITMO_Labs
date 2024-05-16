package org.awesoma.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.client.Client;
import org.awesoma.common.Environment;
import org.awesoma.common.UserCredentials;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.ResourceBundle;

public class AuthController implements LanguageSwitcher{
    @FXML
    public Menu langMenu;
    @FXML
    public Text registerSuggestionTextField;
    @FXML
    public Text askingTextField;
    @FXML
    public MenuBar langMenuBar;
    @FXML
    public TextField loginTextField;
    @FXML
    public TextField passwordTextField;
    @FXML
    public Button registerButton;
    @FXML
    private Button loginButton;

    private static final Logger logger = LogManager.getLogger(AuthController.class);
    private Runnable callback;
    private MainController mainController;
    private String login;
    private String password;
    private Client client;

    @FXML
    private void initialize() {
        client = new Client(Environment.HOST, Environment.PORT);
        client.openSocket();

        initializeTextFields();
    }

    @FXML
    private void loginEvent() {
        try {
            logger.info("login button clicked");
            login = loginTextField.getText();

            if (login.isEmpty()) {
                return;
                //todo
            }

            password = hashPassword(passwordTextField.getText());

            var userCred = new UserCredentials(login, password);
            client.sendLoginRequest(userCred);

            callback.run();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException | IOException e) {
            logger.error(e.getMessage());
            showError(e.getMessage());
        }
    }

    @FXML
    private void registerEvent() {
        try {
            logger.info("register button clicked");
            login = loginTextField.getText();
            if (login.isEmpty()) {
                return;
                //todo
            }

            password = hashPassword(passwordTextField.getText());

            var userCred = new UserCredentials(login, password);
            client.sendRegisterRequest(userCred);

            callback.run();
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
            showError(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void initializeTextFields() {
        loginTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches(".{0,40}")) {
                loginTextField.setText(oldValue);
            }
        });
        passwordTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\S*")) {
                passwordTextField.setText(oldValue);
            }
        });
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String hashPassword(String p) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return new String(md.digest(p.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void switchSpanish() {
        mainController.setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("es")));
        changeLanguage();
    }

    @FXML
    void switchRussian() {
        mainController.setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("ru")));
        changeLanguage();
    }

    @FXML
    void switchDutch() {
        mainController.setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("nl")));
        changeLanguage();
    }

    @FXML
    void switchGerman() {
        mainController.setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("de")));
        changeLanguage();
    }

    @FXML
    void switchEnglish() {
        mainController.setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("en")));
        changeLanguage();
    }

    public void changeLanguage() {
        langMenu.setText(mainController.getCurrentBundle().getString("language"));
        loginTextField.setPromptText(mainController.getCurrentBundle().getString("login"));
        passwordTextField.setPromptText(mainController.getCurrentBundle().getString("password"));
        registerButton.setText(mainController.getCurrentBundle().getString("register"));
        loginButton.setText(mainController.getCurrentBundle().getString("login"));
        askingTextField.setText(mainController.getCurrentBundle().getString("pleaseEnterLoginAndPassword"));
        registerSuggestionTextField.setText(mainController.getCurrentBundle().getString("registerSuggestion"));
    }
}
