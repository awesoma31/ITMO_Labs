package org.awesoma.client.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.client.Client;
import org.awesoma.client.util.DialogManager;
import org.awesoma.client.util.Localizator;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.*;
import org.awesoma.common.util.Validator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class EditController implements LanguageSwitch, IAlert {
    private static final Logger logger = LogManager.getLogger(EditController.class);
    private Localizator localizator;
    @FXML
    public Label movieLabel;
    @FXML
    public Label movieNameLabel;
    @FXML
    public TextField movieNameTextField;
    @FXML
    public Label tboLabel;
    @FXML
    public TextField tboTextField;
    @FXML
    public Label uboLabel;
    @FXML
    public TextField uboTextField;
    @FXML
    public Label ocLabel;
    @FXML
    public TextField ocTextField;
    @FXML
    public Label genreLabel;
    @FXML
    public TextField genreTextField;
    @FXML
    public Label coordinatesLabel;
    @FXML
    public Label coordXLabel;
    @FXML
    public TextField coordXtextField;
    @FXML
    public Label coordYLabel;
    @FXML
    public TextField coordYTextField;
    @FXML
    public Label operatorLabel;
    @FXML
    public Label opNameLabel;
    @FXML
    public TextField opNameTextField;
    @FXML
    public Label opBirthdayLabel;
    @FXML
    public TextField opBDTextField;
    @FXML
    public Label opWeightLabel;
    @FXML
    public TextField opWeightTextField;
    public MenuBar colorMenuBar;
    public Menu colorMenu;
    public MenuItem redColorMenuItem;
    public MenuItem blackColorMenuItem;
    public MenuItem yellowColorMenuItem;
    public MenuItem orangeColorMenuItem;
    public MenuItem whiteColorMenuItem;
    public MenuItem ukCountryMenuItem;
    public MenuItem germanyCountryMenuItem;
    public MenuItem franceCountryMenuItem;
    public MenuItem noneCountryMenuItem1;
    //    public HBox hbox;
    @FXML
    public Button cancelButton;
    public Menu countryMenu;
    public MenuBar countryMenuBar;
    @FXML
    public ComboBox<Color> colorComboBox;
    @FXML
    public ComboBox<Country> nationComboBox1;
    @FXML
    public ComboBox<MovieGenre> genreComboBox;
    @FXML
    public HBox hbox;
    @FXML
    public Button okButton;
    private Client client;
    private Runnable mainStageCallback;
    private MainController mainController;
    private Integer oscarsCount;
    private int totalBoxOffice;
    private Long usaBoxOffice;
    private MovieGenre genre;
    private String owner;
    private String movieName;
    private String operatorName;
    private LocalDateTime creationDate;
    private double coordX;
    private long coordY;
    private float weight;
    private LocalDateTime opBirthday;
    private Color operatorEyeColor;
    private Country operatorNationality;
    private Stage stage;


    public void initialize() {
//        changeLanguage();
        stage = new Stage();
//        stage.setScene(new Scene());
//todo
        colorComboBox.getItems().addAll(Color.values());
        nationComboBox1.getItems().addAll(Country.values());
        genreComboBox.getItems().addAll(MovieGenre.values());
    }


    public void setClient(Client client) {
        this.client = client;
    }

    public void setMainStageCallback(Runnable callback) {
        this.mainStageCallback = callback;
    }

    public void fill(Movie m) {
        var opEyeColor = m.getOperator().getEyeColor() != null ? m.getOperator().getEyeColor() : Color.NONE;
        var genre = m.getGenre() != null ? m.getGenre() : MovieGenre.NONE;

        movieNameTextField.setText(m.getName());
        ocTextField.setText(String.valueOf(m.getOscarsCount()));
        tboTextField.setText(String.valueOf(m.getTotalBoxOffice()));
        uboTextField.setText(String.valueOf(m.getUsaBoxOffice()));
        coordXtextField.setText(String.valueOf(m.getCoordinates().getX()));
        coordYTextField.setText(String.valueOf(m.getCoordinates().getY()));
        opWeightTextField.setText(String.valueOf(m.getOperator().getWeight()));
        opBDTextField.setText(m.getOperator().getBirthday().toString());
        opNameTextField.setText(m.getOperator().getName());
        colorComboBox.getSelectionModel().select(opEyeColor);
        nationComboBox1.getSelectionModel().select(m.getOperator().getNationality());
        genreComboBox.getSelectionModel().select(genre);
    }

    @Override
    public void changeLanguage() {
        opBirthdayLabel.setText(localizator.getKeyString("opBirthdayLabel"));
        opNameLabel.setText(localizator.getKeyString("opNameLabel"));
        opNameTextField.setPromptText(localizator.getKeyString("opNameTextField"));
        ocLabel.setText(localizator.getKeyString("ocLabel"));
        ocTextField.setPromptText(localizator.getKeyString("ocTextField"));
        genreLabel.setText(localizator.getKeyString("genreLabel"));
        genreTextField.setPromptText(localizator.getKeyString("genreTextField"));
        coordinatesLabel.setText(localizator.getKeyString("coordinatesLabel"));
        coordXLabel.setText(localizator.getKeyString("coordXLabel"));
        coordYLabel.setText(localizator.getKeyString("coordYLabel"));
        coordYTextField.setPromptText(localizator.getKeyString("coordYTextField"));
        operatorLabel.setText(localizator.getKeyString("operatorLabel"));
        redColorMenuItem.setText(localizator.getKeyString("redColorMenuItem"));
        blackColorMenuItem.setText(localizator.getKeyString("blackColorMenuItem"));
        yellowColorMenuItem.setText(localizator.getKeyString("yellowColorMenuItem"));
        orangeColorMenuItem.setText(localizator.getKeyString("orangeColorMenuItem"));
        whiteColorMenuItem.setText(localizator.getKeyString("whiteColorMenuItem"));
        ukCountryMenuItem.setText(localizator.getKeyString("ukCountryMenuItem"));
        germanyCountryMenuItem.setText(localizator.getKeyString("germanyCountryMenuItem"));
        franceCountryMenuItem.setText(localizator.getKeyString("franceCountryMenuItem"));
        noneCountryMenuItem1.setText(localizator.getKeyString("noneCountryMenuItem1"));
        cancelButton.setText(localizator.getKeyString("cancelButton"));
        okButton.setText(localizator.getKeyString("okButton"));
        noneCountryMenuItem1.setText(localizator.getKeyString("noneCountryMenuItem1"));
    }

    public void ok(ActionEvent event) {
        if (!opBDTextField.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
            showAlert(Alert.AlertType.WARNING, null, "Warning", "Please enter date in format YYYY-MM-DD");
            return;
        }

        try {
            mainController.setSelectedMovie(Optional.of(buildMovie()));
        } catch (ValidationException | NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, null, "Error", "Please enter valid data: " + e.getMessage());
            // TODO:
        } finally {
            var stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    private void updateDataFromFields() {
        try {
            owner = client.getUserCredentials().username();
            movieName = movieNameTextField.getText().trim();
            oscarsCount = Integer.parseInt(ocTextField.getText().trim());
            totalBoxOffice = Integer.parseInt(tboTextField.getText().trim());
            usaBoxOffice = Long.parseLong(uboTextField.getText().trim());
            coordX = Double.parseDouble(coordXtextField.getText().trim());
            coordY = Long.parseLong(coordYTextField.getText().trim());
            weight = Float.parseFloat(opWeightTextField.getText().trim());
            operatorName = opNameTextField.getText().trim();
            usaBoxOffice = Long.parseLong(uboTextField.getText().trim());
            weight = Float.parseFloat(opWeightTextField.getText().trim());
            operatorName = opNameTextField.getText().trim();
            operatorEyeColor = colorComboBox.getSelectionModel().getSelectedItem();
            operatorNationality = nationComboBox1.getSelectionModel().getSelectedItem();
            creationDate = LocalDateTime.now();
            opBirthday = Validator.convertDateFromString(opBDTextField.getText().trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            DialogManager.alert(localizator.getKeyString("Enter all data"), localizator);
            // TODO: implement
        }

    }

    private Movie buildMovie() throws ValidationException {
        updateDataFromFields();
        return Movie.Builder.aMovie()
                .coordinates(new Coordinates(coordX, coordY))
                .operator(Person.Builder.aPerson()
                        .name(operatorName)
                        .birthday(opBirthday)
                        .weight(weight)
                        .eyeColor(operatorEyeColor)
                        .nationality(operatorNationality)
                        .build()
                )
                .owner(owner)
                .name(movieName)
                .oscarsCount(oscarsCount)
                .totalBoxOffice(totalBoxOffice)
                .usaBoxOffice(usaBoxOffice)
                .creationDate(creationDate)
                .genre(genre)
                .build();
    }

    public void cancel(ActionEvent event) {
        logger.info("cancel clicked");
//        stage.close();

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void colorChosen() {
        operatorEyeColor = colorComboBox.getSelectionModel().getSelectedItem();
    }

    public void nationChosen() {
        operatorNationality = nationComboBox1.getSelectionModel().getSelectedItem();
    }

    public void genreChosen() {
        genre = genreComboBox.getSelectionModel().getSelectedItem();
    }

    public void setLocalizator(Localizator localizator) {
        this.localizator = localizator;
    }

    public void show() {
        stage.showAndWait();
    }

    public void setRoot(Parent root) {
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
