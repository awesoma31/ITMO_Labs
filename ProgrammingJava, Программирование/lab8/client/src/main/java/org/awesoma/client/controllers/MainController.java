package org.awesoma.client.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.client.Client;
import org.awesoma.common.models.Movie;
import org.awesoma.common.models.MovieGenre;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.stream.Collectors;

public class MainController implements LanguageSwitcher {
    private Runnable authCallback;
    private ResourceBundle currentBundle;

    public MenuBar menuBar;
    public Menu langMenu;
    public TableColumn<Movie, String> operatorColumn;
    public TableColumn<Movie, MovieGenre> genreColumn;
    public TableColumn<Movie, Integer> oscarsCountColumn;
    public TableColumn<Movie, Integer> totalBoxOfficeColumn;
    public TableColumn<Movie, String> nameColumn;
    public TableColumn<Movie, Integer> idColumn;
    public TableColumn<Movie, String> ownerColumn;
    @FXML
    public TableColumn<Movie, LocalDateTime> creationDateColumn;
    @FXML
    public TableView<Movie> movieTable;
    @FXML
    public Label usernameLabel;
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
        initializeUsernameLabel();
    }

    private void initializeUsernameLabel() {
        if (client != null) {
            usernameLabel.setText(client.getUserCredentials().username());
        }

    }

    private void fillTable() {
        ObservableList<Movie> movies = FXCollections.observableArrayList();
        Vector<Movie> movieList = getCollectionFromDB();
//        movies.addAll(new Vector<Movie>().stream().toList());

//        idColumn.setCellValueFactory(personData -> personData.getValue().getId());

    }

    private Vector<Movie> getCollectionFromDB() {
        return client.getCollectionFromDB();
    }


    public void executeScript() {

    }

    public void clear() {
    }

    public void help() {
    }

    public void add() {
    }

    public void addIfMax() {

    }

    public void info() {
    }

    public void removeById() {
    }

    public void removeAt() {
    }

    public void update() {
    }

    public void logOut() {
        authCallback.run();
    }

    public void exit() {
        System.exit(0);
    }

    public void setClient(Client client) {
        this.client = client;
        initializeUsernameLabel();
        // todo
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

    @Override
    public void changeLanguage() {
        logOutButton.setText(currentBundle.getString("logOut"));
        executeScriptButton.setText(currentBundle.getString("executeScript"));
        clearButton.setText(currentBundle.getString("clear"));
        helpButton.setText(currentBundle.getString("help"));
        addButton.setText(currentBundle.getString("add"));
        addIfMaxButton.setText(currentBundle.getString("addIfMax"));
        infoButton.setText(currentBundle.getString("info"));
        removeByIdButton.setText(currentBundle.getString("removeById"));
        removeAtButton.setText(currentBundle.getString("removeAt"));
        updateButton.setText(currentBundle.getString("update"));
        exitButton.setText(currentBundle.getString("exit"));

        idColumn.setText(currentBundle.getString("id"));
        nameColumn.setText(currentBundle.getString("name"));
        ownerColumn.setText(currentBundle.getString("owner"));
        creationDateColumn.setText(currentBundle.getString("creationDate"));
        operatorColumn.setText(currentBundle.getString("operator"));
        genreColumn.setText(currentBundle.getString("genre"));
        oscarsCountColumn.setText(currentBundle.getString("oscarsCount"));
        totalBoxOfficeColumn.setText(currentBundle.getString("totalBoxOffice"));

        langMenu.setText(currentBundle.getString("language"));
    }

    @FXML
    void switchSpanish() {
        setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("es")));
        changeLanguage();
    }

    @FXML
    void switchRussian() {
        setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("ru")));
        changeLanguage();
    }

    @FXML
    void switchDutch() {
        setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("nl")));
        changeLanguage();
    }

    @FXML
    void switchGerman() {
        setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("de")));
        changeLanguage();
    }

    @FXML
    void switchEnglish() {
        setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("en")));
        changeLanguage();
    }

}
