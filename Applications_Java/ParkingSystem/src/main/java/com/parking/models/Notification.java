package com.parking.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Notification {
    private final StringProperty id;
    private final StringProperty title;
    private final StringProperty message;
    private final ObjectProperty<NotificationType> type;
    private final ObjectProperty<LocalDateTime> timestamp;
    private final BooleanProperty isRead;

    public Notification(String id, String title, String message, NotificationType type) {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.message = new SimpleStringProperty(message);
        this.type = new SimpleObjectProperty<>(type);
        this.timestamp = new SimpleObjectProperty<>(LocalDateTime.now());
        this.isRead = new SimpleBooleanProperty(false);
    }

    // Getters
    public String getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getMessage() { return message.get(); }
    public NotificationType getType() { return type.get(); }
    public LocalDateTime getTimestamp() { return timestamp.get(); }
    public boolean isRead() { return isRead.get(); }

    // Property getters for JavaFX bindings
    public StringProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty messageProperty() { return message; }
    public ObjectProperty<NotificationType> typeProperty() { return type; }
    public ObjectProperty<LocalDateTime> timestampProperty() { return timestamp; }
    public BooleanProperty isReadProperty() { return isRead; }

    // Setters
    public void setRead(boolean read) { this.isRead.set(read); }
}