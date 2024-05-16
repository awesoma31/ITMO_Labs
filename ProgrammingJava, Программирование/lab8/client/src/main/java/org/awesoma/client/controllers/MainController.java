package org.awesoma.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.client.Client;

import java.util.ResourceBundle;

public class MainController {
    private Runnable authCallback;
    private ResourceBundle currentBundle;

    @FXML
    public Label userLabel;
    @FXML
    public Button executeScriptButton;
    @FXML
    public Button clearButton;
    @FXML
    public Button helpButton;
    @FXML
    public Button addButton;
    @FXML
    public Button addIfMaxButton;
    @FXML
    public Button infoButton;
    @FXML
    public Button removeByIdButton;
    @FXML
    public Button removeAtButton;
    @FXML
    public Button updateButton;
    @FXML
    public Button logOutButton;
    @FXML
    public Button exitButton;
    @FXML
    public AnchorPane mainScene;
    private static final Logger logger = LogManager.getLogger(AuthController.class);
    private Client client;

    @FXML
    public void initialize() {

    }


    public void executeScript(ActionEvent event) {

    }

    public void clear(ActionEvent event) {
    }

    public void help(ActionEvent event) {
    }

    public void add(ActionEvent event) {
    }

    public void addIfMax(ActionEvent event) {

    }

    public void info(ActionEvent event) {
    }

    public void removeById(ActionEvent event) {
    }

    public void removeAt(ActionEvent event) {
    }

    public void update(ActionEvent event) {
    }

    public void logOut(ActionEvent event) {

        authCallback.run();
    }

    public void exit(ActionEvent event) {
        System.exit(0);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setAuthCallback(Runnable authCallback) {
        this.authCallback = authCallback;
    }

    public ResourceBundle getCurrentBundle() {
        return currentBundle;
    }

    public void setCurrentBundle(ResourceBundle currentBundle) {
        this.currentBundle = currentBundle;
    }
}
