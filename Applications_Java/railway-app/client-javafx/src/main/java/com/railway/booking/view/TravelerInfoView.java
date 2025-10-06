package com.railway.booking.view;

import com.railway.booking.util.UIUtils;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TravelerInfoView {

    public static Optional<Map<String, String>> show(int seatCount) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Traveler Information");
        dialog.setHeaderText("Please enter details for " + seatCount + " traveler(s).");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField[] nameFields = new TextField[seatCount];
        TextField[] ageFields = new TextField[seatCount];
        ComboBox<String>[] genderComboBoxes = new ComboBox[seatCount];

        for (int i = 0; i < seatCount; i++) {
            grid.add(new Label("Traveler " + (i + 1) + " Name:"), 0, i * 3);
            nameFields[i] = new TextField();
            nameFields[i].setPromptText("Name");
            grid.add(nameFields[i], 1, i * 3);

            grid.add(new Label("Age:"), 0, i * 3 + 1);
            ageFields[i] = new TextField();
            ageFields[i].setPromptText("Age");
            grid.add(ageFields[i], 1, i * 3 + 1);

            grid.add(new Label("Gender:"), 0, i * 3 + 2);
            genderComboBoxes[i] = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
            grid.add(genderComboBoxes[i], 1, i * 3 + 2);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Map<String, String> travelerInfo = new HashMap<>();
                for (int i = 0; i < seatCount; i++) {
                    String age = ageFields[i].getText();
                    if (!age.matches("\\d+")) {
                        UIUtils.showErrorAlert("Invalid age. Please enter a number.");
                        return null;
                    }
                    travelerInfo.put("Traveler " + (i + 1) + " Name", nameFields[i].getText());
                    travelerInfo.put("Traveler " + (i + 1) + " Age", age);
                    travelerInfo.put("Traveler " + (i + 1) + " Gender", genderComboBoxes[i].getValue());
                }
                return travelerInfo;
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
