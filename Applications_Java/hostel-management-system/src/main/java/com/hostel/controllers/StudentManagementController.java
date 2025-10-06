package com.hostel.controllers;

import com.hostel.database.StudentDAO;
import com.hostel.models.Student;
import com.hostel.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class StudentManagementController implements Observer<List<Student>> {

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, Integer> idColumn;

    @FXML
    private TableColumn<Student, String> nameColumn;

    @FXML
    private TableColumn<Student, String> emailColumn;

    @FXML
    private TableColumn<Student, String> phoneColumn;

    @FXML
    private TableColumn<Student, String> addressColumn;

    @FXML
    private TableColumn<Student, Integer> roomIdColumn;

    private final StudentDAO studentDAO = new StudentDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));

        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        studentDAO.addObserver(this);
        loadStudents();
    }

    @Override
    public void update(List<Student> students) {
        studentTable.setItems(FXCollections.observableArrayList(students));
    }

    private void loadStudents() {
        try {
            ObservableList<Student> students = FXCollections.observableArrayList(studentDAO.getAllStudents());
            studentTable.setItems(students);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void addStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddStudent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadStudents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditStudent.fxml"));
                Parent root = loader.load();

                EditStudentController controller = loader.getController();
                controller.setStudent(selectedStudent);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Edit Student");
                stage.showAndWait();
                loadStudents();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Student");
            alert.setHeaderText("Are you sure you want to delete this student?");
            alert.setContentText(selectedStudent.getName());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        studentDAO.deleteStudent(selectedStudent.getId());
                        loadStudents();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) studentTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        stage.setScene(new Scene(root));
    }
}
