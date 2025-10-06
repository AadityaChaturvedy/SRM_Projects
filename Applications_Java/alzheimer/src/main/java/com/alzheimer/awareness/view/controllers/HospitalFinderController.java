package com.alzheimer.awareness.view.controllers;

import com.alzheimer.awareness.model.Hospital;
import com.alzheimer.awareness.viewmodel.HospitalFinderViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class HospitalFinderController {

    @FXML private TextField locationField;
    @FXML private Button searchBtn;
    @FXML private ListView<Hospital> hospitalList;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> filterCombo;
    @FXML private VBox hospitalDetailsPane;
    @FXML private Label selectedHospitalName;
    @FXML private Label selectedHospitalAddress;
    @FXML private Label selectedHospitalPhone;
    @FXML private Label selectedHospitalDistance;

    private HospitalFinderViewModel viewModel;

    @FXML
    private void initialize() {
        viewModel = new HospitalFinderViewModel();

        setupEventHandlers();
        setupFilterCombo();
        setupHospitalList();
    }

    private void setupEventHandlers() {
        searchBtn.setOnAction(e -> performSearch());
        locationField.setOnAction(e -> performSearch());

        filterCombo.setOnAction(e -> applyFilter());

        hospitalList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> showHospitalDetails(newSelection)
        );
    }

    private void setupFilterCombo() {
        filterCombo.getItems().addAll(
            "All Results",
            "Within 5 km",
            "Within 10 km",
            "Within 20 km",
            "Sort by Name"
        );
        filterCombo.setValue("All Results");
    }

    private void setupHospitalList() {
        hospitalList.setCellFactory(listView -> new ListCell<Hospital>() {
            @Override
            protected void updateItem(Hospital hospital, boolean empty) {
                super.updateItem(hospital, empty);

                if (empty || hospital == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.format("%s\n%s\nDistance: %.1f km", 
                        hospital.getName(), 
                        hospital.getAddress(), 
                        hospital.getDistanceKm()));
                }
            }
        });
    }

    private void performSearch() {
        String location = locationField.getText().trim();

        if (location.isEmpty()) {
            statusLabel.setText("Please enter a location or PIN code");
            return;
        }

        statusLabel.setText("Searching...");

        try {
            List<Hospital> results = viewModel.searchHospitals(location);

            hospitalList.getItems().clear();
            hospitalList.getItems().addAll(results);

            if (results.isEmpty()) {
                statusLabel.setText("No hospitals found for the given location");
            } else {
                statusLabel.setText(String.format("Found %d care centers near %s", 
                    results.size(), location));
            }

        } catch (Exception e) {
            statusLabel.setText("Search failed: " + e.getMessage());
            System.err.println("Hospital search error: " + e.getMessage());
        }
    }

    private void applyFilter() {
        String selectedFilter = filterCombo.getValue();
        List<Hospital> filteredResults;

        switch (selectedFilter) {
            case "Within 5 km":
                filteredResults = viewModel.filterByDistance(5.0);
                break;
            case "Within 10 km":
                filteredResults = viewModel.filterByDistance(10.0);
                break;
            case "Within 20 km":
                filteredResults = viewModel.filterByDistance(20.0);
                break;
            case "Sort by Name":
                filteredResults = viewModel.sortByName();
                break;
            default:
                filteredResults = viewModel.getSearchResults();
                break;
        }

        hospitalList.getItems().clear();
        hospitalList.getItems().addAll(filteredResults);

        statusLabel.setText(String.format("Showing %d results", filteredResults.size()));
    }

    private void showHospitalDetails(Hospital hospital) {
        if (hospital != null) {
            selectedHospitalName.setText(hospital.getName());
            selectedHospitalAddress.setText(hospital.getAddress());
            selectedHospitalPhone.setText("Phone: " + hospital.getPhone());
            selectedHospitalDistance.setText(String.format("Distance: %.1f km", hospital.getDistanceKm()));

            hospitalDetailsPane.setVisible(true);
        } else {
            hospitalDetailsPane.setVisible(false);
        }
    }
}
