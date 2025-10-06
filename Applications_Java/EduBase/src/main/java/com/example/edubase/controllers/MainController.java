package com.example.edubase.controllers;

import com.example.edubase.models.Student;
import com.example.edubase.models.Subject;
import com.example.edubase.services.StudentService;
import com.example.edubase.utils.AlertUtils;
import com.example.edubase.utils.ThemeManager;
import com.example.edubase.utils.ToastUtil;
import com.example.edubase.utils.Validator;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;

public class MainController {
    @FXML private TextField appAttField, coaAttField, dsaAttField, mathsAttField, osAttField;
    @FXML private Label avgAttendanceLabel;

    @FXML private TextField appC1Field, appC2Field, appC3Field, appC4Field;
    @FXML private TextField coaC1Field, coaC2Field, coaC3Field, coaC4Field;
    @FXML private TextField dsaC1Field, dsaC2Field, dsaC3Field, dsaC4Field;
    @FXML private TextField mathsC1Field, mathsC2Field, mathsC3Field, mathsC4Field;
    @FXML private TextField osC1Field, osC2Field, osC3Field, osC4Field;
    @FXML private Label avgMarksLabel;
    @FXML private Label appTotalLabel, coaTotalLabel, dsaTotalLabel, mathsTotalLabel, osTotalLabel;

    @FXML private TextField nameField, ageField, addressField, cityField, stateField, phoneField, emailField;
    @FXML private Button addButton, deleteButton, saveButton, logoutButton;
    @FXML private ToggleButton themeToggleButton;
    @FXML private TableView<Student> studentTableView;
    @FXML private TableColumn<Student, Long> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private SplitPane mainSplitPane;
    @FXML private TitledPane editorPane;
    @FXML private TextField searchField;

    private final StudentService studentService = new StudentService();
    private Student currentStudent;
    private FilteredList<Student> filteredStudents;
    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private final Map<TextField, Predicate<String>> validationMap = new HashMap<>();

    @FXML
    public void initialize() {
        setupTable();
        loadStudentData();
        setupFormBindingsAndValidation();
        setupEventListeners();
        setupSearchFilter();
        Platform.runLater(() -> {
            setupTheme();
            setupKeyboardShortcuts();
            mainSplitPane.setDividerPositions(0.35);
        });
        clearForm();
        editorPane.setDisable(true);

        logoutButton.setOnAction(e -> handleLogout());

        // Attach listeners for real-time total/average calculation
        addCycleListeners(appC1Field, appC2Field, appC3Field, appC4Field, appTotalLabel);
        addCycleListeners(coaC1Field, coaC2Field, coaC3Field, coaC4Field, coaTotalLabel);
        addCycleListeners(dsaC1Field, dsaC2Field, dsaC3Field, dsaC4Field, dsaTotalLabel);
        addCycleListeners(mathsC1Field, mathsC2Field, mathsC3Field, mathsC4Field, mathsTotalLabel);
        addCycleListeners(osC1Field, osC2Field, osC3Field, osC4Field, osTotalLabel);

        // Attendance updates average as you type
        appAttField.textProperty().addListener((obs, o, n) -> calculateAverages());
        coaAttField.textProperty().addListener((obs, o, n) -> calculateAverages());
        dsaAttField.textProperty().addListener((obs, o, n) -> calculateAverages());
        mathsAttField.textProperty().addListener((obs, o, n) -> calculateAverages());
        osAttField.textProperty().addListener((obs, o, n) -> calculateAverages());
    }

    private void addCycleListeners(TextField c1, TextField c2, TextField c3, TextField c4, Label totalLabel) {
        c1.textProperty().addListener((obs, oldV, newV) -> updateSubjectTotal(c1, c2, c3, c4, totalLabel));
        c2.textProperty().addListener((obs, oldV, newV) -> updateSubjectTotal(c1, c2, c3, c4, totalLabel));
        c3.textProperty().addListener((obs, oldV, newV) -> updateSubjectTotal(c1, c2, c3, c4, totalLabel));
        c4.textProperty().addListener((obs, oldV, newV) -> updateSubjectTotal(c1, c2, c3, c4, totalLabel));
    }

    private void updateSubjectTotal(TextField c1, TextField c2, TextField c3, TextField c4, Label totalLabel) {
        int s1 = getInt(c1), s2 = getInt(c2), s3 = getInt(c3), s4 = getInt(c4);
        int sum = (s1 >= 0 ? s1 : 0) + (s2 >= 0 ? s2 : 0) + (s3 >= 0 ? s3 : 0) + (s4 >= 0 ? s4 : 0);
        totalLabel.setText(String.valueOf(sum));
        calculateAverages();
    }

    private void calculateAverages() {
        int[] totals = {
            getInt(appTotalLabel), getInt(coaTotalLabel),
            getInt(dsaTotalLabel), getInt(mathsTotalLabel), getInt(osTotalLabel)
        };
        int marksSum = 0, marksCnt = 0;
        for (int v : totals) { if (v > 0) { marksSum += v; marksCnt++; } }
        avgMarksLabel.setText(marksCnt == 0 ? "-" : (marksSum / marksCnt) + "%");

        int[] att = {
            getInt(appAttField), getInt(coaAttField),
            getInt(dsaAttField), getInt(mathsAttField), getInt(osAttField)
        };
        int attSum = 0, attCnt = 0;
        for (int v : att) { if (v > 0) { attSum += v; attCnt++; } }
        avgAttendanceLabel.setText(attCnt == 0 ? "-" : (attSum / attCnt) + "%");
    }
    private int getInt(TextField tf) {
        try { return Integer.parseInt(tf.getText().trim()); } catch (Exception e) { return -1; }
    }
    private int getInt(Label lbl) {
        try { return Integer.parseInt(lbl.getText().trim()); } catch (Exception e) { return -1; }
    }

    private void setupTheme() {
        ThemeManager.Theme currentTheme = ThemeManager.loadThemePreference();
        themeToggleButton.setSelected(currentTheme == ThemeManager.Theme.DARK);
        ThemeManager.applyTheme(themeToggleButton.getScene(), currentTheme);
        themeToggleButton.setText(currentTheme == ThemeManager.Theme.DARK ? "☀️" : "🌙");
        themeToggleButton.setOnAction(event -> {
            ThemeManager.Theme newTheme = themeToggleButton.isSelected()
                    ? ThemeManager.Theme.DARK
                    : ThemeManager.Theme.LIGHT;
            ThemeManager.applyTheme(themeToggleButton.getScene(), newTheme);
            themeToggleButton.setText(newTheme == ThemeManager.Theme.DARK ? "☀️" : "🌙");
        });
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void loadStudentData() {
        ObservableList<Student> students = studentService.getAllStudents();
        filteredStudents = new FilteredList<>(students, p -> true);
        studentTableView.setItems(filteredStudents);
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredStudents.setPredicate(student -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return student.getName().toLowerCase().contains(lower)
                        || student.getEmail().toLowerCase().contains(lower)
                        || student.getCity().toLowerCase().contains(lower);
            });
        });
    }

    private void setupFormBindingsAndValidation() {
        addValidationRule(nameField, Validator::isNameValid, "Name must contain only letters and spaces.");
        addValidationRule(ageField, Validator::isAgeValid, "Age must be a number between 5 and 120.");
        addValidationRule(addressField, s -> !Validator.isFieldEmpty(s), "Address is required.");
        addValidationRule(cityField, Validator::isCityOrStateValid, "City must contain only letters and spaces.");
        addValidationRule(stateField, Validator::isCityOrStateValid, "State must contain only letters and spaces.");
        addValidationRule(phoneField, Validator::isPhoneValid, "Phone must be exactly 10 digits.");
        addValidationRule(emailField, Validator::isEmailValid, "Enter a valid email address.");
        for (TextField tf : Arrays.asList(
            appAttField, coaAttField, dsaAttField, mathsAttField, osAttField,
            appC1Field, appC2Field, appC3Field, appC4Field,
            coaC1Field, coaC2Field, coaC3Field, coaC4Field,
            dsaC1Field, dsaC2Field, dsaC3Field, dsaC4Field,
            mathsC1Field, mathsC2Field, mathsC3Field, mathsC4Field,
            osC1Field, osC2Field, osC3Field, osC4Field
        )) {
            addValidationRule(tf, Validator::isAcademicValueValid, "Must be a number between 0 and 100.");
        }
    }

    private void addValidationRule(TextField field, Predicate<String> logic, String tip) {
        field.focusedProperty().addListener((obs, was, isNow) -> {
            if (was && !isNow) validateField(field, logic, tip);
        });
        field.setTooltip(new Tooltip(tip));
        validationMap.put(field, logic);
    }
    private void validateField(TextField field, Predicate<String> logic, String tip) {
        boolean isValid = logic.test(field.getText());
        field.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isValid);
        field.getTooltip().setText(isValid ? "Valid" : tip);
    }
    private boolean isFormValid() {
        return validationMap.entrySet().stream()
                .allMatch(entry -> entry.getValue().test(entry.getKey().getText()));
    }

    private void setupEventListeners() {
        studentTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> { if (newSel != null) populateForm(newSel); }
        );
    }

    private void populateForm(Student student) {
        currentStudent = student;
        editorPane.setDisable(false);
        editorPane.setText("Editing Student: " + student.getName());
        nameField.setText(student.getName());
        ageField.setText(String.valueOf(student.getAge()));
        addressField.setText(student.getAddress());
        cityField.setText(student.getCity());
        stateField.setText(student.getState());
        phoneField.setText(student.getPhone());
        emailField.setText(student.getEmail());
        fillSubjectFields("APP", appAttField, appC1Field, appC2Field, appC3Field, appC4Field, appTotalLabel);
        fillSubjectFields("COA", coaAttField, coaC1Field, coaC2Field, coaC3Field, coaC4Field, coaTotalLabel);
        fillSubjectFields("DSA", dsaAttField, dsaC1Field, dsaC2Field, dsaC3Field, dsaC4Field, dsaTotalLabel);
        fillSubjectFields("Maths", mathsAttField, mathsC1Field, mathsC2Field, mathsC3Field, mathsC4Field, mathsTotalLabel);
        fillSubjectFields("OS", osAttField, osC1Field, osC2Field, osC3Field, osC4Field, osTotalLabel);
        validationMap.keySet().forEach(field -> field.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false));
        calculateAverages();
    }

    // Util for populateForm and updateStudentFromForm
    private void fillSubjectFields(String subject,
                                   TextField att, TextField c1, TextField c2, TextField c3, TextField c4, Label totalLabel) {
        Subject sub = currentStudent.getSubjects().get(subject);
        if (sub != null) {
            att.setText(String.valueOf(sub.getAttendance()));
            c1.setText(String.valueOf(sub.getCycle1()));
            c2.setText(String.valueOf(sub.getCycle2()));
            c3.setText(String.valueOf(sub.getCycle3()));
            c4.setText(String.valueOf(sub.getCycle4()));
            totalLabel.setText(String.valueOf(sub.getTotal()));
        } else {
            att.clear(); c1.clear(); c2.clear(); c3.clear(); c4.clear(); totalLabel.setText("0");
        }
    }

    private void clearForm() {
        currentStudent = null;
        editorPane.setDisable(true);
        editorPane.setText("Student Details");
        studentTableView.getSelectionModel().clearSelection();
        validationMap.keySet().forEach(field -> { field.clear(); field.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false); });
        avgAttendanceLabel.setText("-");
        avgMarksLabel.setText("-");
        appTotalLabel.setText("0");
        coaTotalLabel.setText("0");
        dsaTotalLabel.setText("0");
        mathsTotalLabel.setText("0");
        osTotalLabel.setText("0");
    }

    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/edubase/fxml/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            ThemeManager.applyTheme(scene, ThemeManager.loadThemePreference());
            stage.setScene(scene);
            stage.setTitle("EduBase - Login");
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtils.showError("Failed to open login screen: " + ex.getMessage(), null);
        }
    }

    @FXML
    private void handleAdd() {
        clearForm();
        currentStudent = new Student();
        editorPane.setDisable(false);
        editorPane.setText("Add New Student");
        nameField.requestFocus();
    }

    @FXML
    private void handleSave() {
        if (!isFormValid()) {
            ToastUtil.showError("Please fix the errors before saving.");
            return;
        }
        if (currentStudent == null) {
            ToastUtil.showError("No student selected or being created.");
            return;
        }
        Optional<ButtonType> result = AlertUtils.showConfirmation("Confirm Save", "Are you sure you want to save these changes?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateStudentFromForm();
            try {
                studentService.saveStudent(currentStudent);
                ToastUtil.showSuccess("Student saved successfully!");
                loadStudentData();
                clearForm();
            } catch (SQLException e) {
                AlertUtils.showError("Database Error: Could not save student. Email may already exist.", null);
            }
        }
    }

    private void updateStudentFromForm() {
        currentStudent.setName(nameField.getText());
        currentStudent.setAge(parseIntSafe(ageField));
        currentStudent.setAddress(addressField.getText());
        currentStudent.setCity(cityField.getText());
        currentStudent.setState(stateField.getText());
        currentStudent.setPhone(phoneField.getText());
        currentStudent.setEmail(emailField.getText());
        setSubjectFields("APP", appAttField, appC1Field, appC2Field, appC3Field, appC4Field, appTotalLabel);
        setSubjectFields("COA", coaAttField, coaC1Field, coaC2Field, coaC3Field, coaC4Field, coaTotalLabel);
        setSubjectFields("DSA", dsaAttField, dsaC1Field, dsaC2Field, dsaC3Field, dsaC4Field, dsaTotalLabel);
        setSubjectFields("Maths", mathsAttField, mathsC1Field, mathsC2Field, mathsC3Field, mathsC4Field, mathsTotalLabel);
        setSubjectFields("OS", osAttField, osC1Field, osC2Field, osC3Field, osC4Field, osTotalLabel);
        calculateAverages();
    }

    private void setSubjectFields(String subject,
                                  TextField att, TextField c1, TextField c2, TextField c3, TextField c4, Label totalLabel) {
        Subject sub = currentStudent.getSubjects().computeIfAbsent(subject, Subject::new);
        sub.setAttendance(parseIntSafe(att));
        sub.setCycle1(parseIntSafe(c1));
        sub.setCycle2(parseIntSafe(c2));
        sub.setCycle3(parseIntSafe(c3));
        sub.setCycle4(parseIntSafe(c4));
        sub.setTotal(getInt(totalLabel));
    }

    @FXML
    private void handleDelete() {
        Student s = studentTableView.getSelectionModel().getSelectedItem();
        if (s == null) {
            AlertUtils.showError("No Selection", "Please select a student to delete.");
            return;
        }
        Optional<ButtonType> result = AlertUtils.showConfirmation(
            "Confirm Deletion",
            "Are you sure you want to delete " + s.getName() + "?"
        );
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                studentService.deleteStudent(s);
                ToastUtil.showSuccess("Student deleted successfully.");
                loadStudentData();
                clearForm();
            } catch (SQLException e) {
                AlertUtils.showError("Database Error", "Could not delete student.");
            }
        }
    }

    private void setupKeyboardShortcuts() {
        Scene scene = nameField.getScene();
        if (scene == null) return;
        scene.getAccelerators().put(
            new javafx.scene.input.KeyCodeCombination(javafx.scene.input.KeyCode.S, javafx.scene.input.KeyCombination.CONTROL_DOWN),
            this::handleSave
        );
        scene.getAccelerators().put(
            new javafx.scene.input.KeyCodeCombination(javafx.scene.input.KeyCode.ESCAPE),
            this::handleCancel
        );
    }

    @FXML
    private void handleCancel() {
        if (!editorPane.isDisable()) {
            Optional<ButtonType> result = AlertUtils.showConfirmation("Confirm Cancel", "Are you sure you want to discard changes?");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                clearForm();
            }
        }
    }

    private int parseIntSafe(TextField tf) {
        try { return Integer.parseInt(tf.getText().trim()); } catch (Exception e) { return 0; }
    }
}