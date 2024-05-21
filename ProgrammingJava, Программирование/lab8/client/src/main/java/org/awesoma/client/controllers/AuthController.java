package org.awesoma.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.client.Client;
import org.awesoma.client.util.Localizator;
import org.awesoma.common.UserCredentials;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.ResourceBundle;

public class AuthController implements LanguageSwitch, IAlert {
    private static final Logger logger = LogManager.getLogger(AuthController.class);
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
    private Runnable callback;
    private MainController mainController;
    private String login;
    private String password;
    private Client client;
    private Localizator localizator;

    @FXML
    private void initialize() {
        initializeTextFields();
    }

    @FXML
    private void loginEvent() {
        try {
            logger.info("login button clicked");
            login = loginTextField.getText();

            if (login.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, null, "Error", "Please enter login");
                return;
            }

            password = client.hashPassword(passwordTextField.getText());

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

            password = client.hashPassword(passwordTextField.getText());

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
        showAlert(Alert.AlertType.ERROR, null, "Error", message);
    }
//
//    private String hashPassword(String p) {
//        // todo to client
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            return new String(md.digest(p.getBytes()));
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void switchSpanish() {
        localizator.setBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("es")));
        changeLanguage();
    }

    @FXML
    void switchRussian() {
        localizator.setBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("ru")));
        changeLanguage();
    }

    @FXML
    void switchDutch() {
        localizator.setBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("nl")));
        changeLanguage();
    }

    @FXML
    void switchGerman() {
        localizator.setBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("de")));
        changeLanguage();
    }

    @FXML
    void switchEnglish() {
        localizator.setBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("en")));
        changeLanguage();
    }

    public void changeLanguage() {
        langMenu.setText(localizator.getKeyString("language"));
        loginTextField.setPromptText(localizator.getKeyString("login"));
        passwordTextField.setPromptText(localizator.getKeyString("password"));
        registerButton.setText(localizator.getKeyString("register"));
        loginButton.setText(localizator.getKeyString("login"));
        askingTextField.setText(localizator.getKeyString("pleaseEnterLoginAndPassword"));
        registerSuggestionTextField.setText(localizator.getKeyString("registerSuggestion"));
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setLocalizator(Localizator localizator) {
        this.localizator = localizator;
    }
}
