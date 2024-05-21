package org.awesoma.client.controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.client.App;
import org.awesoma.client.Client;
import org.awesoma.client.util.DialogManager;
import org.awesoma.client.util.Localizator;
import org.awesoma.client.util.Session;
import org.awesoma.common.commands.*;
import org.awesoma.common.models.Color;
import org.awesoma.common.models.Movie;
import org.awesoma.common.models.MovieGenre;

import java.io.IOException;
import java.util.*;

public class MainController implements LanguageSwitch, IAlert {
    private static final Logger logger = LogManager.getLogger(AuthController.class);
    private Localizator localizator;
    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu langMenu;
    @FXML
    public TableColumn<Movie, String> operatorNameColumn;
    @FXML
    public TableColumn<Movie, String> genreColumn;
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
    public TableColumn<Movie, String> creationDateColumn;
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
    public TableColumn<Movie, String> opBirthdayColumn;
    public TableColumn<Movie, Float> opWeightColumn;
    public TableColumn<Movie, String> opEyeColorColumn;
    public TableColumn<Movie, String> opNationColumn;
    public TableColumn<Movie, Long> yColumn;
    public TableColumn<Movie, Double> xColumn;

    private Runnable authCallback;
    private Runnable editCallback;
    private ResourceBundle currentBundle;
    private Client client;
    private Vector<Movie> collection;
    private EditController editController;
    private Optional<Movie> selectedMovie;

    @FXML
    public void initialize() {
        if (currentBundle == null) {
//            currentBundle = ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("en"));
//            currentBundle = localizator.getBundle();
        }

//        changeLanguage();

        initializeUsernameLabel();
        initializeTable();
    }

    private void initializeTable() {
        ownerColumn.setCellValueFactory(m -> new SimpleStringProperty(m.getValue().getOwner()));
        idColumn.setCellValueFactory(m -> new SimpleIntegerProperty(m.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(m -> new SimpleStringProperty(m.getValue().getName()));
        creationDateColumn.setCellValueFactory(m -> new SimpleStringProperty(localizator.getDate(m.getValue().getCreationDate())));
        operatorNameColumn.setCellValueFactory(m -> new SimpleStringProperty(m.getValue().getOperator().getName()));
        genreColumn.setCellValueFactory(m -> new SimpleStringProperty(m.getValue().getGenre() != null ? m.getValue().getGenre().name() : MovieGenre.NONE.name()));
        oscarsCountColumn.setCellValueFactory(m -> new SimpleIntegerProperty(m.getValue().getOscarsCount()).asObject());
        totalBoxOfficeColumn.setCellValueFactory(m -> new SimpleIntegerProperty(m.getValue().getTotalBoxOffice()).asObject());
        opBirthdayColumn.setCellValueFactory(m -> new SimpleStringProperty(localizator.getDate(m.getValue().getOperator().getBirthday())));
        opEyeColorColumn.setCellValueFactory(m -> new SimpleStringProperty(
                m.getValue().getOperator().getEyeColor() != null ?
                        m.getValue().getOperator().getEyeColor().name() :
                        Color.NONE.name())
        );
        opNationColumn.setCellValueFactory(m -> new SimpleStringProperty(m.getValue().getOperator().getNationality().name()));
        opWeightColumn.setCellValueFactory(m -> new SimpleFloatProperty(m.getValue().getOperator().getWeight()).asObject());
        xColumn.setCellValueFactory(m -> new SimpleDoubleProperty(m.getValue().getCoordinates().getX()).asObject());
        yColumn.setCellValueFactory(m -> new SimpleLongProperty(m.getValue().getCoordinates().getY()).asObject());
    }

    private void initializeUsernameLabel() {
        if (client != null && client.getUserCredentials() != null) {
            usernameLabel.setText(client.getUserCredentials().username());
        }

    }

    public void fillTable() {
        collection = getCollectionFromDB();
        movieTable.setItems(FXCollections.observableArrayList(collection));
    }

    @FXML
    public void update() {
        logger.info("update clicked");
        Optional<String> input = DialogManager.createDialog(localizator.getKeyString("Update"), "ID:");
        if (input.isPresent() && !input.get().isEmpty()) {
            try {
                var id = Integer.parseInt(input.orElse(""));
                var movie = collection.stream()
                        .filter(p -> p.getId() == id)
                        .findAny()
                        .orElse(null);
                if (movie == null) {
                    DialogManager.alert("NotFoundException", localizator);
                } else if (!Objects.equals(movie.getOwner(), Session.getCurrentUser())) {
                    // todo
                    DialogManager.alert("WrongOwnerException", localizator);
                    throw new RuntimeException();
                }

//                doubleClickUpdate(movie, false);
                //todo

            } catch (NumberFormatException e) {
                DialogManager.alert("NumberFormatException", localizator);
            }
        }
    }

    private Vector<Movie> getCollectionFromDB() {
        return client.getCollectionFromDB();
    }

    @FXML
    public void executeScript() {
        logger.info("executeScript clicked");
        //todo
    }

    @FXML
    public void clear() {
        logger.info("clear clicked");
        try {
            client.sendThenHandleResponse(client.getCommand(ClearCommand.NAME), new ArrayList<>());
        } catch (IOException e) {
            DialogManager.alert("IOException", localizator);
        }
        fillTable();
    }

    @FXML
    public void help() {
        logger.info("help clicked");
        //todo
    }

    @FXML
    public void add() {
        logger.info("add clicked");

        launchEditController();

        if (selectedMovie.isPresent()) {
            try {
                client.sendThenHandleResponse(client.getCommand(AddCommand.NAME), new ArrayList<>(), selectedMovie.get());
                fillTable();
            } catch (IOException e) {
                DialogManager.alert("IOException", localizator);
            }
        } else {
            DialogManager.alert("Movie is not presented", localizator);
        }
    }

    @FXML
    public void addIfMax() {
        logger.info("addIfMax clicked");

        launchEditController();

        if (selectedMovie.isPresent()) {
            try {
                client.sendThenHandleResponse(client.getCommand(AddIfMaxCommand.NAME), new ArrayList<>(), selectedMovie.get());
                // todo response handling
                fillTable();
            } catch (IOException e) {
                DialogManager.alert("IOException", localizator);
            }
        } else {
            DialogManager.alert("Movie is not presented", localizator);
        }
    }

    private void launchEditController() {
        var editLoader = new FXMLLoader(App.class.getResource("fxml/edit-view.fxml"));
        var editRoot = loadFxml(editLoader);
        EditController controller = editLoader.getController();

        controller.setLocalizator(localizator);
        controller.setClient(client);
        controller.setMainController(this);

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

    @FXML
    public void info() {
        logger.info("info clicked");
    }

    @FXML
    public void removeById() {
        logger.info("removeById clicked");

        var res = DialogManager.createDialog("Remove movie by ID", "ID: ");
        if (res.isPresent() && !res.get().equals("")) {
            try {
                var args = new ArrayList<String>();
                var index = Integer.parseInt(res.get());
                args.add(String.valueOf(index));
//                client.sendCommand(client.getCommand(RemoveAtCommand.NAME), args);
//                var r = client.receiveResponse();
//                if (r.getStatusCode() != Status.OK) {
                //todo
//                    DialogManager.alert(r.getStatusCode().name(), localizator);
//                }
                client.sendThenHandleResponse(client.getCommand(RemoveByIdCommand.NAME), args);
            } catch (NumberFormatException e) {
                DialogManager.alert("NumberFormatException", localizator);
            } catch (IOException e) {
                DialogManager.alert("IOException", localizator);
            }
        }
        fillTable();
    }

    public void removeAt() {
        logger.info("removeAt clicked");

        var res = DialogManager.createDialog("Remove movie at index", "index: ");
        if (res.isPresent() && !res.get().equals("")) {
            try {
                var args = new ArrayList<String>();
                var index = Integer.parseInt(res.get());
                args.add(String.valueOf(index));
//                client.sendCommand(client.getCommand(RemoveAtCommand.NAME), args);
//                var r = client.receiveResponse();
//                if (r.getStatusCode() != Status.OK) {
                //todo
//                    DialogManager.alert(r.getStatusCode().name(), localizator);
//                }
                client.sendThenHandleResponse(client.getCommand(RemoveAtCommand.NAME), args);
            } catch (NumberFormatException e) {
                DialogManager.alert("NumberFormatException", localizator);
            } catch (IOException e) {
                DialogManager.alert("IOException", localizator);
            }
        }
        fillTable();
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

    @Override
    public void changeLanguage() {
        logOutButton.setText(localizator.getKeyString("logOut"));
        userLabel.setText(localizator.getKeyString("User:"));
        executeScriptButton.setText(localizator.getKeyString("executeScript"));
        clearButton.setText(localizator.getKeyString("clear"));
        helpButton.setText(localizator.getKeyString("help"));
        addButton.setText(localizator.getKeyString("add"));
        addIfMaxButton.setText(localizator.getKeyString("addIfMax"));
        infoButton.setText(localizator.getKeyString("info"));
        removeByIdButton.setText(localizator.getKeyString("removeById"));
        removeAtButton.setText(localizator.getKeyString("removeAt"));
        updateButton.setText(localizator.getKeyString("update"));
        exitButton.setText(localizator.getKeyString("exit"));

        idColumn.setText(localizator.getKeyString("id"));
        nameColumn.setText(localizator.getKeyString("name"));
        ownerColumn.setText(localizator.getKeyString("owner"));
        creationDateColumn.setText(localizator.getKeyString("creationDate"));
        operatorNameColumn.setText(localizator.getKeyString("operator"));
        genreColumn.setText(localizator.getKeyString("genre"));
        oscarsCountColumn.setText(localizator.getKeyString("oscarsCount"));
        totalBoxOfficeColumn.setText(localizator.getKeyString("totalBoxOffice"));

        langMenu.setText(localizator.getKeyString("language"));
    }

    @FXML
    void switchSpanish() {
//        setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("es")));
        localizator.setBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("es")));
        changeLanguage();
    }

    @FXML
    void switchRussian() {
//        setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("ru")));
        localizator.setBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("ru")));
        changeLanguage();
    }

    @FXML
    void switchDutch() {
//        setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("nl")));
        localizator.setBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("nl")));
        changeLanguage();
    }

    @FXML
    void switchGerman() {
//        setCurrentBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("de")));
        localizator.setBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("de")));
        changeLanguage();
    }

    @FXML
    void switchEnglish() {
        localizator.setBundle(ResourceBundle.getBundle("org.awesoma.client.bundles.Language", new Locale("en")));
//        setCurrentBundle();
        changeLanguage();
    }

    public void setEditCallback(Runnable editCallback) {
        this.editCallback = editCallback;
    }

    public Localizator getLocalizator() {
        return localizator;
    }

    public void setLocalizator(Localizator localizator) {
        this.localizator = localizator;
    }

    public void setEditController(EditController editController) {
        this.editController = editController;
    }

    public Optional<Movie> getSelectedMovie() {
        return selectedMovie;
    }

    public void setSelectedMovie(Optional<Movie> selectedMovie) {
        this.selectedMovie = selectedMovie;
    }
}
