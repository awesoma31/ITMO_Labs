package org.awesoma.client.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.client.App;
import org.awesoma.client.Client;
import org.awesoma.client.util.DialogManager;
import org.awesoma.client.util.Localizator;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.*;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.models.Color;
import org.awesoma.common.models.Movie;
import org.awesoma.common.models.MovieGenre;
import org.awesoma.common.network.Response;
import org.awesoma.common.network.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MainController implements LanguageSwitch, IAlert {
    private static final Logger logger = LogManager.getLogger(AuthController.class);
    public HBox deleteButtonHBox;
    public Button deleteButton;
    public Button changeButton;
    public TabPane tabPane;
    public Tab tableTab;
    public Tab visualizationTab;
    public AnchorPane visAnchorPane;
    public Pane visPane;
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
    private Client client;
    private Vector<Movie> collection;
    private Optional<Movie> selectedMovie;
    private boolean isEditing = false;
    private double initialX;
    private double initialY;

    private ConcurrentHashMap<Integer, Circle> drawnMovies = new ConcurrentHashMap<>();

    @FXML
    public void initialize() {
        initializeUsernameLabel();
        initializeTable();

        initVisualMovement();

        deleteButtonHBox.setVisible(false);

        visualizationTab.setOnSelectionChanged(e -> visualize());

        new Thread(() -> {
            while (!isEditing) {
                try {
                    Platform.runLater(this::fillTable);
                    Platform.runLater(this::visualize);
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("InterruptedException: ", e);
                }
            }
        }).start();
    }

    public synchronized void visualize() {
        visAnchorPane.getChildren().clear();

        SequentialTransition animation = new SequentialTransition();

//        for (Integer movieId : drawnMovies.keySet()) {
//            for (Movie m : collection) {
//                if (!Objects.equals(m.getId(), movieId)) {
//
//                    drawnMovies.remove(movieId);
//                }
//            }
//        }

        Iterator<Map.Entry<Integer, Circle>> iterator = drawnMovies.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Circle> entry = iterator.next();
            boolean exists = false;
            for (Movie m : collection) {
                if (Objects.equals(m.getId(), entry.getKey())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), entry.getValue());
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> visAnchorPane.getChildren().remove(entry.getValue()));
                animation.getChildren().add(fadeOut);

                //visAnchorPane.getChildren().remove(entry.getValue());
                iterator.remove();
            }
        }

        for (Movie m : collection) {
            if (!drawnMovies.containsKey(m.getId())) {
                draw(m, animation);
            }
        }
        animation.play();

    }

    private void draw(Movie m, SequentialTransition animation) {
        Circle circle = new Circle(m.getCoordinates().getX(), m.getCoordinates().getY(), ((double) m.getTotalBoxOffice() / 10 + 10), javafx.scene.paint.Color.valueOf(colorByID(m.getId())));

        circle.setOnMouseClicked(event -> {
            selectedMovie = Optional.of(m);
            launchEditController(m);
            selectedMovie.get().setId(m.getId());
            selectedMovie.get().setOwner(client.getUserCredentials().username());
            var args = new ArrayList<String>();
            args.add(String.valueOf(m.getId()));

            if (selectedMovie.isPresent()) {
                try {
                    var c = client.getCommand(UpdateIdCommand.NAME);
                    var r = client.sendThenGetResponse(c, args, selectedMovie.get());
                    showResponse(r, c, false);
                } catch (IOException e) {
                    DialogManager.alert("IOException", localizator);
                }
            }
        });

        var label = new Label(m.getName());
        label.setLabelFor(circle);
        label.setLayoutX(circle.getCenterX() - label.getWidth() / 2);
        label.setLayoutY(circle.getCenterY() - label.getHeight() / 2);


//        circle.setOpacity(0.0);
//        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), circle);
//        fadeIn.setToValue(1.0);
//        animation.getChildren().add(fadeIn);

        visAnchorPane.getChildren().addAll(circle, label);
        drawnMovies.put(m.getId(), circle);
    }

    private void initVisualMovement() {
        visAnchorPane.setOnMousePressed(event -> {
            initialX = event.getSceneX();
            initialY = event.getSceneY();
        });

        visAnchorPane.setOnMouseDragged(event -> {
            double offsetX = event.getSceneX() - initialX;
            double offsetY = event.getSceneY() - initialY;

            for (javafx.scene.Node node : visAnchorPane.getChildren()) {
                if (node instanceof Circle c) {
                    c.setCenterX(c.getCenterX() + offsetX);
                    c.setCenterY(c.getCenterY() + offsetY);
                }
                if (node instanceof Label label) {
                    label.setLayoutX(label.getLayoutX() + offsetX);
                    label.setLayoutY(label.getLayoutY() + offsetY);
                }
            }

            initialX = event.getSceneX();
            initialY = event.getSceneY();
        });
    }

    private String colorByID(int ID) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            byte[] hash = mDigest.digest(Integer.toString(ID).getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            return hex.substring(0, 6);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Can't get color by ID", e);
            return "#babbbc";
        }
    }


    private void initializeTable() {
        initColumns();

        initSelectingListener();
    }

    private void initSelectingListener() {
        movieTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedMovie = Optional.of(newSelection);
                deleteButtonHBox.setVisible(true);
                deleteButton.setVisible(true);
                changeButton.setVisible(true);
            } else {
                deleteButtonHBox.setVisible(false);
                deleteButton.setVisible(false);
                changeButton.setVisible(false);
            }
        });
    }

    private void initColumns() {
        ownerColumn.setCellValueFactory(m -> new SimpleStringProperty(m.getValue().getOwner()));
        idColumn.setCellValueFactory(m -> new SimpleIntegerProperty(m.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(m -> new SimpleStringProperty(m.getValue().getName()));
        creationDateColumn.setCellValueFactory(m -> new SimpleStringProperty(localizator.getDate(m.getValue().getCreationDate().toLocalDate())));
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
        try {
            collection = getCollectionFromDB();
            ObservableList<Movie> data = FXCollections.observableArrayList(collection);
            movieTable.setItems(data);
        } catch (IllegalStateException e) {
            logger.error(e);
        }
//        visualize();
    }

    public void fillTableWithAnimation() {
        collection = getCollectionFromDB();

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.7), movieTable);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            movieTable.setItems(FXCollections.observableArrayList(collection));

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.7), movieTable);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    public synchronized void change(ActionEvent event) {
        isEditing = true;
        if (selectedMovie.isPresent()) {
            var m = collection.stream()
                    .filter(p -> Objects.equals(p.getOwner(), selectedMovie.get().getOwner()))
                    .findAny()
                    .orElse(null);
            if (m == null) {
                DialogManager.alert("NotFoundException", localizator);
            } else if (!Objects.equals(m.getOwner(), client.getUserCredentials().username())) {
                DialogManager.alert("WrongOwnerException", localizator);
            } else {
                launchEditController(m);

                var args = new ArrayList<String>();
                args.add(String.valueOf(m.getId()));

                try {
                    var c = client.getCommand(UpdateIdCommand.NAME);
                    var r = client.sendThenGetResponse(c, args, selectedMovie.get());
                    showResponse(r, c, false);
                } catch (IOException e) {
                    DialogManager.alert("IOException", localizator);
                } finally {
//                    fillTableWithAnimation();
                }
            }
        }
        isEditing = false;
    }


    @FXML
    public void updateById() {
        logger.info("update clicked");
        Optional<String> idGiven = DialogManager.createDialog(localizator.getKeyString("Update"), "ID:");
        if (idGiven.isPresent() && !idGiven.get().isEmpty()) {
            try {
                var id = Integer.parseInt(idGiven.orElse(""));
                var m = collection.stream()
                        .filter(p -> p.getId() == id)
                        .findAny()
                        .orElse(null);
                if (m == null) {
                    DialogManager.alert("NotFoundException", localizator);
                } else if (!Objects.equals(m.getOwner(), client.getUserCredentials().username())) {
                    DialogManager.alert("WrongOwnerException", localizator);
                } else {
                    launchEditController(m);
                    selectedMovie = Optional.of(m);
                    selectedMovie.get().setId(id);
                    selectedMovie.get().setOwner(client.getUserCredentials().username());
                    var args = new ArrayList<String>();
                    args.add(String.valueOf(id));

                    if (selectedMovie.isPresent()) {
                        try {
                            var c = client.getCommand(UpdateIdCommand.NAME);
                            var r = client.sendThenGetResponse(c, args, selectedMovie.get());
                            showResponse(r, c, false);
                        } catch (IOException e) {
                            DialogManager.alert("IOException", localizator);
                        } finally {
//                            fillTableWithAnimation();
                        }
                    } else {
                        DialogManager.alert("Movie is not presented", localizator);
                    }
                }

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
    public void executeScript(ActionEvent event) {
        //todo infinite loop show
        logger.info("executeScript clicked");
        var r = DialogManager.createFileDialog(event, localizator);
        if (r.isPresent() && !r.get().isEmpty()) {
            var args = new ArrayList<String>();
            args.add(r.get());
            try {
                client.executeScript(args, new BufferedReader(new InputStreamReader(System.in)));
            } catch (CommandExecutingException e) {
                DialogManager.createAlert("CommandExecutingException", e.getMessage(), Alert.AlertType.ERROR, true);
            }
        }
    }

    @FXML
    public void clear() {
        logger.info("clear clicked");
        try {
            client.sendThenHandleResponse(client.getCommand(ClearCommand.NAME), new ArrayList<>());
        } catch (IOException e) {
            DialogManager.alert("IOException", localizator);
        }
        fillTableWithAnimation();
    }

    @FXML
    public void help() {
        logger.info("help clicked");
        String d = buildHelpInfo();

        DialogManager.createAlert("Help", d, Alert.AlertType.INFORMATION, true);
    }

    private String buildHelpInfo() {
        String data = "[AVAILABLE COMMANDS]:\n" + Environment.getAvailableCommands().values().stream()
                .filter(Command::isShownInHelp)
                .map(Command::getHelp)
                .collect(Collectors.joining("\n"));
        return data;
//                "[AVAILABLE COMMANDS]:" + "\n" + Environment.getAvailableCommands().values().stream()
//                .filter(Command::isShowInHelp)
//                .map(command -> "<" + localizator.getKeyString(command.getName()) + ">: " + localizator.getKeyString(command.getDescription()))
//                .collect(Collectors.joining("\n"));
    }

    @FXML
    public void add() {
        logger.info("add clicked");

        launchEditController();

        if (selectedMovie != null && selectedMovie.isPresent()) {
            try {
                var c = client.getCommand(AddCommand.NAME);
                var r = client.sendThenGetResponse(c, new ArrayList<>(), selectedMovie.get());
                showResponse(r, c, false);
            } catch (IOException e) {
                DialogManager.alert("IOException", localizator);
            } finally {
//                fillTableWithAnimation();
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
                var c = client.getCommand(AddIfMaxCommand.NAME);
                var r = client.sendThenGetResponse(c, new ArrayList<>(), selectedMovie.get());
                showResponse(r, c, false);
            } catch (IOException e) {
                DialogManager.alert("IOException", localizator);
            } finally {
//                fillTableWithAnimation();
            }
        } else {
            DialogManager.alert("Movie is not presented", localizator);
        }
    }

    private void launchEditController() {
        var editLoader = new FXMLLoader(App.class.getResource("fxml/edit-view.fxml"));
        var editRoot = loadFxml(editLoader);
        EditController controller = editLoader.getController();

        prepareAndLaunchEdit(editRoot, controller);
    }

    private void launchEditController(Movie m) {
        var editLoader = new FXMLLoader(App.class.getResource("fxml/edit-view.fxml"));
        var editRoot = loadFxml(editLoader);
        EditController controller = editLoader.getController();

        controller.fill(m);
        prepareAndLaunchEdit(editRoot, controller);
    }

    private void prepareAndLaunchEdit(Parent editRoot, EditController controller) {
        controller.setLocalizator(localizator);
        controller.setClient(client);
        controller.setMainController(this);
        controller.changeLanguage();

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
        try {
            logger.info("info clicked");
            var c = client.getCommand(InfoCommand.NAME);
            var r = client.sendThenGetResponse(c, new ArrayList<>());

            showResponse(r, c, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static <T extends Command> void showResponse(Response r, T c, boolean isReturnedTextNeeded) {
        if (r.getStatusCode() != Status.OK) {
            var alertType = r.getStatusCode() == Status.WARNING ? Alert.AlertType.WARNING : Alert.AlertType.ERROR;
            DialogManager.createAlert(r.getStatusCode().name(), r.getMessage(), alertType, true);
        } else if (isReturnedTextNeeded) {
            DialogManager.createAlert(c.getName(), r.getMessage(), Alert.AlertType.INFORMATION, true);
        }
    }

    @FXML
    public void removeById() {
        logger.info("removeById clicked");

        var res = DialogManager.createDialog("Remove movie by ID", "ID: ");
        if (res.isPresent() && !res.get().isEmpty()) {
            try {
                var args = new ArrayList<String>();
                var index = Integer.parseInt(res.get());
                args.add(String.valueOf(index));

                var c = client.getCommand(RemoveByIdCommand.NAME);
                var r = client.sendThenGetResponse(c, args);
                showResponse(r, c, false);
                drawnMovies.remove(collection.get(index));
            } catch (NumberFormatException e) {
                DialogManager.alert("NumberFormatException", localizator);
            } catch (IOException e) {
                DialogManager.alert("IOException", localizator);
            }
        }
        fillTableWithAnimation();
    }

    public void removeAt() {
        logger.info("removeAt clicked");

        var res = DialogManager.createDialog("Remove movie at index", "index: ");
        if (res.isPresent() && !res.get().isEmpty()) {
            try {
                var args = new ArrayList<String>();
                var index = Integer.parseInt(res.get());
                args.add(String.valueOf(index));

                var c = client.getCommand(RemoveAtCommand.NAME);
                var r = client.sendThenGetResponse(c, args);
                showResponse(r, c, false);
                drawnMovies.remove(collection.get(index));
            } catch (NumberFormatException e) {
                DialogManager.alert("NumberFormatException", localizator);
            } catch (IOException e) {
                DialogManager.alert("IOException", localizator);
            }
        }
        fillTableWithAnimation();
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

        deleteButton.setText(localizator.getKeyString("delete"));

        idColumn.setText(localizator.getKeyString("id"));
        nameColumn.setText(localizator.getKeyString("name"));
        ownerColumn.setText(localizator.getKeyString("owner"));
        creationDateColumn.setText(localizator.getKeyString("creationDate"));
        operatorNameColumn.setText(localizator.getKeyString("operator"));
        opBirthdayColumn.setText(localizator.getKeyString("opBirthdayLabel"));
        opWeightColumn.setText(localizator.getKeyString("opWeightLabel"));
        genreColumn.setText(localizator.getKeyString("genre"));
        opEyeColorColumn.setText(localizator.getKeyString("Eye color"));
        opNationColumn.setText(localizator.getKeyString("Nationality"));
        oscarsCountColumn.setText(localizator.getKeyString("oscarsCount"));
        totalBoxOfficeColumn.setText(localizator.getKeyString("totalBoxOffice"));

        tableTab.setText(localizator.getKeyString("tableTab"));
        visualizationTab.setText(localizator.getKeyString("visualizationTab"));

        langMenu.setText(localizator.getKeyString("language"));
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

    public void setLocalizator(Localizator localizator) {
        this.localizator = localizator;
    }

    public void setEditController(EditController editController) {
    }

    public Optional<Movie> getSelectedMovie() {
        return selectedMovie;
    }

    public void setSelectedMovie(Optional<Movie> selectedMovie) {
        this.selectedMovie = selectedMovie;
    }

    public Runnable getEditCallback() {
        return editCallback;
    }

    public void setEditCallback(Runnable editCallback) {
        this.editCallback = editCallback;
    }

    public void delete(ActionEvent event) {
        var c = client.getCommand(RemoveByIdCommand.NAME);
        try {
            if (selectedMovie.isPresent()) {
                var id = selectedMovie.get().getId();
                var a = new ArrayList<String>();
                a.add(String.valueOf(id));
                var r = client.sendThenGetResponse(client.getCommand(RemoveByIdCommand.NAME), a);
                showResponse(r, c, false);
                drawnMovies.remove(collection.get(id));
            }
        } catch (IOException e) {
            DialogManager.alert("IOException", localizator);
        } finally {
            fillTableWithAnimation();
        }
    }
}
