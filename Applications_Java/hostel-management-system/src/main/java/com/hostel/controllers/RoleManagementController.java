package com.hostel.controllers;

import com.hostel.database.UserDAO;
import com.hostel.models.User;
import com.hostel.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RoleManagementController implements Observer<List<User>> {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> permissionsColumn;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        permissionsColumn.setCellValueFactory(new PropertyValueFactory<>("permissions"));

        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Register as observer to UserDAO (assuming UserDAO is observable)
        // userDAO.addObserver(this);
        loadUsers();
    }

    @Override
    public void update(List<User> users) {
        userTable.setItems(FXCollections.observableArrayList(users));
    }

    private void loadUsers() {
        try {
            ObservableList<User> users = FXCollections.observableArrayList(userDAO.getAllUsers());
            userTable.setItems(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editUserRoles() {
        // TODO: Implement edit user roles functionality
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) userTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        stage.setScene(new Scene(root));
    }
}
