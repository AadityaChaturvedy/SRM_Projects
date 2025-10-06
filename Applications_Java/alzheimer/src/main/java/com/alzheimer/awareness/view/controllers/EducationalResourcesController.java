package com.alzheimer.awareness.view.controllers;

import com.alzheimer.awareness.model.EducationalResource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EducationalResourcesController implements Initializable {

    @FXML
    private ListView<EducationalResource> resourcesListView;

    @FXML
    private Button backButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Gson gson = new Gson();
        Type resourceListType = new TypeToken<List<EducationalResource>>() {}.getType();
        List<EducationalResource> educationalResources = gson.fromJson(
                new InputStreamReader(getClass().getResourceAsStream("/quiz/educational_resources.json")),
                resourceListType
        );

        resourcesListView.getItems().addAll(educationalResources);

        resourcesListView.setCellFactory(param -> new ListCell<EducationalResource>() {
            @Override
            protected void updateItem(EducationalResource item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + "\n\n" + item.getContent());
                }
            }
        });

        backButton.setOnAction(event -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });
    }
}
