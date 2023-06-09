package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Workout;
import model.WorkoutHub;
import model.WorkoutSchedule;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class AddWorkoutController {

    Stage stage;
    Parent scene;

    // Table columns for the workout table
    @FXML
    private TableColumn<Workout, Integer> calorieCol;
    @FXML
    private TableColumn<Workout, Integer> indexCol;
    @FXML
    private TableColumn<Workout, String> workoutNameCol;

    // Buttons and text fields
    @FXML
    private Button saveButton;
    @FXML
    private Button mainButton;
    @FXML
    private Button addScheduleButton;
    @FXML
    private AnchorPane workoutField;
    @FXML
    private TextField workoutTextField;

    // Table view for displaying workouts
    @FXML
    private TableView<Workout> workoutTable;

    // Event handler for main button that returns the user to the main menu
    @FXML
    void mainButtonHandler(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Main.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    // Event handler for save button that adds a new workout to the workout list
    @FXML
    void saveButtonHandler(ActionEvent event) throws IOException {
        // Get workout Name and feed into constructor
        String workoutName = workoutTextField.getText();
        Workout workout = new Workout(workoutName);

        // Set the index of the new workout to be equal to the size of the workouts list
        int workoutIndex = WorkoutHub.getWorkoutList().size();
        workout.setIndices(workoutIndex);

        // Add the new workout to the workouts list in the WorkoutHub
        WorkoutHub.addWorkout(workout);
        workoutTextField.clear();

        // Create a new observable list and add all the workouts to it
        ObservableList<Workout> workoutObservableList = FXCollections.observableArrayList();
        workoutObservableList.addAll(WorkoutHub.getWorkoutObservableList());

        // Set the workoutTable items to the new observable list
        workoutTable.setItems(workoutObservableList);

        // Set the cell value factories for the columns
        calorieCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCalories()));
        workoutNameCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getWorkoutName()));
        indexCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getIndices()));

        // Show a pop-up alert with the workout name
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Workout Saved");
        alert.setHeaderText(null);
        alert.setContentText("Workout \"" + workoutName + "\" has been saved.");
        alert.showAndWait();
    }


    @FXML
    void addScheduleButtonHandler(ActionEvent actionEvent) {
        // Get the selected workout from the table
        Workout selectedWorkout = workoutTable.getSelectionModel().getSelectedItem();

        if (selectedWorkout == null) {
            // No workout selected, display an alert
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No Workout Selected");
            alert.setContentText("Please select a workout from the table.");
            alert.showAndWait();
        } else {
            // Workout selected, show date and time picker in a dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add to Schedule");

            // Set the button types for the dialog
            ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(addButton, cancelButton);

            // Create the content for the dialog
            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(20, 150, 10, 10));

            // Add a DatePicker, ComboBox for hour, ComboBox for minute, and ComboBox for AM/PM to the GridPane
            DatePicker datePicker = new DatePicker(LocalDate.now());
            ComboBox<Integer> hourComboBox = new ComboBox<>();
            ComboBox<Integer> minuteComboBox = new ComboBox<>();
            ComboBox<String> amPmComboBox = new ComboBox<>();

            // Populate the ComboBoxes with appropriate options
            // The hourComboBox shows values from 1-12
            for (int i = 1; i <= 12; i++) {
                hourComboBox.getItems().add(i);
            }
            // Set the current hour in the hourComboBox based on the application's time zone
            LocalTime currentTime = LocalTime.now(ZoneId.systemDefault());
            hourComboBox.getSelectionModel().select(currentTime.getHour() % 12);

            // The minuteComboBox shows values from 0-55 in increments of 5
            for (int i = 0; i <= 55; i += 5) {
                minuteComboBox.getItems().add(i);
            }
            // Select the 0 minute in the minuteComboBox
            minuteComboBox.getSelectionModel().select(0);

            // The amPmComboBox shows options for AM or PM
            amPmComboBox.getItems().addAll("AM", "PM");
            // Select the current AM/PM value in the amPmComboBox
            amPmComboBox.getSelectionModel().select(LocalTime.now().format(DateTimeFormatter.ofPattern("a")));

            // Add the labels and ComboBoxes to the GridPane
            gridPane.add(new Label("Date:"), 0, 0);
            gridPane.add(datePicker, 1, 0);
            gridPane.add(new Label("Time:"), 0, 1);
            gridPane.add(hourComboBox, 1, 1);
            gridPane.add(new Label(":"), 2, 1);
            gridPane.add(minuteComboBox, 3, 1);
            gridPane.add(amPmComboBox, 4, 1);

            // Add a result converter to the dialog to handle when the "Add" button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButton) {
                    // Check if any of the ComboBoxes is empty
                    if (hourComboBox.getValue() == null || minuteComboBox.getValue() == null || amPmComboBox.getValue() == null) {
                        // Display an alert message if any ComboBox is empty
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText("Incomplete Time Selection");
                        alert.setContentText("Please select a value for each time component.");
                        alert.showAndWait();
                    } else {
                        // Get the selected date and time from the DatePicker and ComboBoxes
                        LocalDate date = datePicker.getValue();
                        int hour = hourComboBox.getValue();
                        int minute = minuteComboBox.getValue();
                        String amPm = amPmComboBox.getValue();
                        // If PM is selected, add 12 to the hour value
                        if (amPm.equals("PM")) {
                            hour += 12;
                        }
                        // Combine the date and time into a LocalDateTime object
                        LocalTime time = LocalTime.of(hour % 24, minute);
                        LocalDateTime dateTime = LocalDateTime.of(date, time);
                        WorkoutSchedule.addWorkoutReminder(selectedWorkout, dateTime);

//                        // Show success alert after adding to the schedule
//                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
//                        successAlert.setTitle("Success");
//                        successAlert.setHeaderText("Workout Added to Schedule");
//                        successAlert.setContentText("The selected workout has been added to your schedule.");
//                        successAlert.showAndWait();
                    }
                }
                return null;
            });

            dialog.getDialogPane().setContent(gridPane);
            dialog.showAndWait();
        }
    }


    // Initialize the controller
    @FXML
    void initialize() {
        // Create a new observable list and add all the workouts to it
        ObservableList<Workout> workoutObservableList = FXCollections.observableArrayList();
        workoutObservableList.addAll(WorkoutHub.getWorkoutObservableList());

        // Set the workoutTable items to the new observable list
        workoutTable.setItems(workoutObservableList);

        // Set the cell value factories for the columns
        calorieCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCalories()));
        workoutNameCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getWorkoutName()));
        indexCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getIndices()));
    }

}