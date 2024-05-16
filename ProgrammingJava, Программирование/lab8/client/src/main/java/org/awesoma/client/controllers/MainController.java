package org.awesoma.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class MainController implements LanguageSwitch {
    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu langMenu;
    @FXML
    public TableColumn<Movie, String> operatorColumn;
    @FXML
    public TableColumn<Movie, MovieGenre> genreColumn;
    @FXML
    public TableColumn<Movie, Integer> oscarsCountColumn;
    @FXML
    public TableColumn<Movie, Integer> totalBoxOfficeColumn;
    @FXML
    public TableColumn<Movie, String> nameColumn;
    @FXML
    public TableColumn<Movie, Integer> idColumn;
    @FXML
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

    private Runnable authCallback;
    private Runnable editCallback;
    private ResourceBundle currentBundle;
    private Client client;

    @FXML
    public void initialize() {
        initializeUsernameLabel();
        initializeTable();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        operatorColumn.setCellValueFactory(new PropertyValueFactory<>("operator"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        oscarsCountColumn.setCellValueFactory(new PropertyValueFactory<>("oscarsCount"));
        totalBoxOfficeColumn.setCellValueFactory(new PropertyValueFactory<>("totalBoxOffice"));
    }

    private void initializeUsernameLabel() {
        if (client != null && client.getUserCredentials() != null) {
            usernameLabel.setText(client.getUserCredentials().username());
        }

    }

    public void fillTable() {
        var col = getCollectionFromDB();
        logger.info(col.toString());

        ObservableList<Movie> data = FXCollections.observableArrayList(col);

        movieTable.setItems(data);
    }

    @FXML
    public void update() {
        logger.info("update clicked");

    }

    private Vector<Movie> getCollectionFromDB() {
        return client.getCollectionFromDB();
    }


    @FXML
    public void executeScript() {
        logger.info("executeScript clicked");
    }

    @FXML
    public void clear() {
        logger.info("clear clicked");
    }

    @FXML
    public void help() {
        logger.info("help clicked");
    }

    @FXML
    public void add() {
        logger.info("add clicked");

        editCallback.run();
    }

    @FXML
    public void addIfMax() {
        logger.info("addIfMax clicked");
    }

    @FXML
    public void info() {
        logger.info("info clicked");
    }

    @FXML
    public void removeById() {
        logger.info("removeById clicked");
    }

    @FXML
    public void removeAt() {
        logger.info("removeAt clicked");
    }

    @FXML
    public void logOut() {
        logger.info("logOut clicked");
        authCallback.run();
    }

    @FXML
    public void exit() {
        logger.info("exit clicked");
        System.exit(0);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        initializeUsernameLabel();
        // todo
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

    public void setEditCallback(Runnable editCallback) {
        this.editCallback = editCallback;
    }
}
