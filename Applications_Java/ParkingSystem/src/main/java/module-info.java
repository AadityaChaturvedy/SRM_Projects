module com.parking.smartparkingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires com.fazecast.jSerialComm;
    requires com.google.gson;

    opens com.parking.controllers to javafx.fxml;
    exports com.parking;
}