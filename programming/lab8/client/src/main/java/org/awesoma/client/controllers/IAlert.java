package org.awesoma.client.controllers;

import javafx.scene.control.Alert;

public interface IAlert {
    default void showAlert(Alert.AlertType alertType, String header, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
