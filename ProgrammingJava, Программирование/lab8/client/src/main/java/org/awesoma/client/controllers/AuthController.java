package org.awesoma.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.client.Client;
import org.awesoma.common.Environment;
import org.awesoma.common.UserCredentials;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthController {
    public Label infoLabel;
    private Runnable callback;
    private static final Logger logger = LogManager.getLogger(AuthController.class);
    @FXML
    public TextField loginTextField;
    @FXML
    public TextField passwordTextField;
    @FXML
    public Button registerButton;
    @FXML
    private Button loginButton;
    private String login;
    private String password;
    private Client client;

    @FXML
    private void initialize() {
        //todo language
        client = new Client(Environment.HOST, Environment.PORT);
        client.openSocket();

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
            showMessage(e.getMessage());
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
            showMessage(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void showMessage(String message) {
        infoLabel.setText(message);
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
}
