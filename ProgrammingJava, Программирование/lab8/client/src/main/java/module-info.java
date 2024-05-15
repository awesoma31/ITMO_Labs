module org.awesoma.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.awesoma.common;


    opens org.awesoma.client to javafx.fxml;
    exports org.awesoma.client;
    exports org.awesoma.client.controllers;
    opens org.awesoma.client.controllers to javafx.fxml;
}