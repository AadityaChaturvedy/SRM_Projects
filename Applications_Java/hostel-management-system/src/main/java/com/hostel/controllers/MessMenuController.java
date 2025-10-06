package com.hostel.controllers;

import com.hostel.database.MessMenuDAO;
import com.hostel.models.MessMenu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class MessMenuController {

    @FXML
    private TableView<MessMenu> menuTable;
    @FXML
    private TableColumn<MessMenu, String> dayCol;
    @FXML
    private TableColumn<MessMenu, String> breakfastCol;
    @FXML
    private TableColumn<MessMenu, String> lunchCol;
    @FXML
    private TableColumn<MessMenu, String> snacksCol;
    @FXML
    private TableColumn<MessMenu, String> dinnerCol;

    @FXML
    private ComboBox<String> dayOfWeekComboBox;
    @FXML
    private TextField breakfastField;
    @FXML
    private TextField lunchField;
    @FXML
    private TextField snacksField;
    @FXML
    private TextField dinnerField;

    private MessMenuDAO messMenuDAO;
    private ObservableList<MessMenu> menuList;

    @FXML
    public void initialize() {
        messMenuDAO = new MessMenuDAO();
        try {
            messMenuDAO.createMessMenuTable();
            messMenuDAO.populateMockData();
            loadMenuData();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create or load mess menu.");
        }

        dayCol.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        breakfastCol.setCellValueFactory(new PropertyValueFactory<>("breakfast"));
        lunchCol.setCellValueFactory(new PropertyValueFactory<>("lunch"));
        snacksCol.setCellValueFactory(new PropertyValueFactory<>("snacks"));
        dinnerCol.setCellValueFactory(new PropertyValueFactory<>("dinner"));

        menuTable.setItems(menuList);

        dayOfWeekComboBox.setItems(FXCollections.observableArrayList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
    }

    private void loadMenuData() throws SQLException {
        menuList = FXCollections.observableArrayList(messMenuDAO.getAllMessMenus());
        menuTable.setItems(menuList);
    }

    @FXML
    private void handleAddMenu() {
        String dayOfWeek = dayOfWeekComboBox.getValue();
        String breakfast = breakfastField.getText();
        String lunch = lunchField.getText();
        String snacks = snacksField.getText();
        String dinner = dinnerField.getText();

        if (dayOfWeek == null || breakfast.isEmpty() || lunch.isEmpty() || snacks.isEmpty() || dinner.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all the fields");
            return;
        }

        try {
            if (messMenuDAO.getMessMenuByDay(dayOfWeek) != null) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Entry", "A menu for this day already exists.");
                return;
            }

            MessMenu newMenu = new MessMenu(0, dayOfWeek, breakfast, lunch, snacks, dinner);
            messMenuDAO.addMessMenu(newMenu);
            loadMenuData();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add mess menu.");
        }
    }

    @FXML
    private void handleUpdateMenu() {
        MessMenu selectedMenu = menuTable.getSelectionModel().getSelectedItem();
        if (selectedMenu == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a menu item to update.");
            return;
        }

        String dayOfWeek = dayOfWeekComboBox.getValue();
        String breakfast = breakfastField.getText();
        String lunch = lunchField.getText();
        String snacks = snacksField.getText();
        String dinner = dinnerField.getText();

        if (dayOfWeek == null || breakfast.isEmpty() || lunch.isEmpty() || snacks.isEmpty() || dinner.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all the fields");
            return;
        }

        try {
            MessMenu existingMenu = messMenuDAO.getMessMenuByDay(dayOfWeek);
            if (existingMenu != null && existingMenu.getMenuId() != selectedMenu.getMenuId()) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Entry", "A menu for this day already exists.");
                return;
            }

            selectedMenu.setDayOfWeek(dayOfWeek);
            selectedMenu.setBreakfast(breakfast);
            selectedMenu.setLunch(lunch);
            selectedMenu.setSnacks(snacks);
            selectedMenu.setDinner(dinner);

            messMenuDAO.updateMessMenu(selectedMenu);
            loadMenuData();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update mess menu.");
        }
    }

    @FXML
    private void handleDeleteMenu() {
        MessMenu selectedMenu = menuTable.getSelectionModel().getSelectedItem();
        if (selectedMenu == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a menu item to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this menu item?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    messMenuDAO.deleteMessMenu(selectedMenu.getMenuId());
                    loadMenuData();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete mess menu.");
                }
            }
        });
    }

    @FXML
    private void handleRowSelect() {
        MessMenu selectedMenu = menuTable.getSelectionModel().getSelectedItem();
        if (selectedMenu != null) {
            dayOfWeekComboBox.setValue(selectedMenu.getDayOfWeek());
            breakfastField.setText(selectedMenu.getBreakfast());
            lunchField.setText(selectedMenu.getLunch());
            snacksField.setText(selectedMenu.getSnacks());
            dinnerField.setText(selectedMenu.getDinner());
        }
    }

    private void clearFields() {
        dayOfWeekComboBox.setValue(null);
        breakfastField.clear();
        lunchField.clear();
        snacksField.clear();
        dinnerField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
