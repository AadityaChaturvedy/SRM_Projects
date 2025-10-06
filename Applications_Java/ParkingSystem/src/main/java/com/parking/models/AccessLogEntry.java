package com.parking.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccessLogEntry {
    private final StringProperty timestamp;
    private final StringProperty cardUID;
    private final StringProperty status;
    private final StringProperty formattedTime;

    public AccessLogEntry(String cardUID, String status) {
        this.timestamp = new SimpleStringProperty(LocalDateTime.now().toString());
        this.cardUID = new SimpleStringProperty(cardUID);
        this.status = new SimpleStringProperty(status);
        this.formattedTime = new SimpleStringProperty(formatTime());
    }

    private String formatTime() {
        LocalDateTime time = LocalDateTime.parse(timestamp.get());
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getTimestamp() { return timestamp.get(); }
    public String getCardUID() { return cardUID.get(); }
    public String getStatus() { return status.get(); }
    public String getFormattedTime() { return formattedTime.get(); }

    public StringProperty timestampProperty() { return timestamp; }
    public StringProperty cardUIDProperty() { return cardUID; }
    public StringProperty statusProperty() { return status; }
    public StringProperty formattedTimeProperty() { return formattedTime; }
}