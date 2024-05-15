package org.awesoma.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    }


    @FXML
    private void loginEvent() {
        try {
            logger.info("login button clicked");
            login = loginTextField.getText();

            password = hashPassword(passwordTextField.getText());

            var userCred = new UserCredentials(login, password);
            client.sendLoginRequest(userCred);

            //todo change scene
        } catch (IOException e) {
            //todo
        } catch (ClassNotFoundException e) {
            //todo change scene
        }

    }

    @FXML
    private void registerEvent() {
        try {
            logger.info("register button clicked");
            login = loginTextField.getText();

            password = hashPassword(passwordTextField.getText());

            var userCred = new UserCredentials(login, password);
            client.sendRegisterRequest(userCred);

            //todo change scene
        } catch (IOException e) {
            // todo
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }


    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    private String hashPassword(String p) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return new String(md.digest(p.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
