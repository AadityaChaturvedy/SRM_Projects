package com.parking.models;

public enum NotificationType {
    INFO("Information", "info-icon"),
    WARNING("Warning", "warning-icon"),
    ERROR("Error", "error-icon"),
    SUCCESS("Success", "success-icon");

    private final String displayName;
    private final String iconClass;

    NotificationType(String displayName, String iconClass) {
        this.displayName = displayName;
        this.iconClass = iconClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconClass() {
        return iconClass;
    }
}