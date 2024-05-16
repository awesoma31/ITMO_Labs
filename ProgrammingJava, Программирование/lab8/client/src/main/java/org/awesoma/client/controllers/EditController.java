package org.awesoma.client.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.client.Client;

public class EditController implements LanguageSwitch {
    private static final Logger logger = LogManager.getLogger(EditController.class);
    public Label movieLabel;
    public Label movieNameLabel;
    public TextField movieNameTextField;
    public Label tboLabel;
    public TextField tboTextField;
    public Label uboLabel;
    public TextField uboTestField;
    public Label ocLabel;
    public TextField ocTextField;
    public Label genreLabel;
    public TextField genreTextField;
    public Label coordinatesLabel;
    public Label coordXLabel;
    public TextField coordXtextField;
    public Label coordYLabel;
    public TextField coordYTextField;
    public Label operatorLabel;
    public Label opNameLabel;
    public TextField opNameTextField;
    public Label opBirthdayLabel;
    public TextField opBDTextField;
    public Label opWeightLabel;
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
    public HBox okButton;
    public Button cancelButton;
    public Menu countryMenu;
    public MenuBar countryMenuBar;
    private Client client;
    private Runnable mainStageCallback;
    private Runnable cancelCallback;
    private MainController mainController;



    public void setClient(Client client) {
        this.client = client;
    }

    public void setMainStageCallback(Runnable callback) {
        this.mainStageCallback = callback;
    }

    public void setCancelCallback(Runnable callback) {
        this.cancelCallback = callback;
    }

    @Override
    public void changeLanguage() {
        
        // TODO: implement
    }

    public void ok(MouseEvent mouseEvent) {
        // TODO: implement
    }

    public void cancel(ActionEvent event) {
        logger.info("cancel clicked");
        mainStageCallback.run();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
