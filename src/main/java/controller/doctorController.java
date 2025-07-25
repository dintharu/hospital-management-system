package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import model.dto.Doctor;
import org.controlsfx.control.Notifications;
import service.ServiceFactory;
import service.custom.DoctorService;
import service.custom.PatientService;
import util.ServiceType;

import javax.print.Doc;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class doctorController implements Initializable {

    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colSpeciality;
    public TableColumn colDate;
    public TableColumn colContact;
    public TableColumn colQualifications;
    public JFXTextField txtName;
    public JFXTextField txtContact;
    public JFXTextArea txtQualifications;
    public JFXComboBox Combospecial;
    public TableView tblDoctors;
    public TableColumn colAvailableDay;
    public TableColumn colAvailableTime;
    public JFXComboBox ComboWeekDays;
    public Spinner startTime;
    public JFXComboBox StartMeridiem;
    public Spinner endTime;
    public JFXComboBox endMeridiem;

    private int nextId = 1;

    String originalName = "";

    private final DoctorService doctorService;

    public doctorController() {
        try {
            this.doctorService = ServiceFactory.getInstance().getServiceType(ServiceType.DOCTOR);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize PatientService", e);
        }
    }

    public void btnAddOnAction(ActionEvent event) {
        if(validateInput()){
            try {
                Doctor  doctor = createDoctorFromForm();

                System.out.println("Doctor time available: " + doctor.getAvailableTime());
                doctorService.add(doctor);
                loadTable();
                clearForm();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            clearForm();
            Notifications notificationBuilder = Notifications.create()
                    .title("Added Successfully")
                    .text("saved to/home/downloads")
                    .graphic(null)
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_RIGHT)
                    .onAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            System.out.println("clicked on notification");
                        }

                    });
            notificationBuilder.showConfirm();
        }

    }

    public void btnReloadOnAction(ActionEvent event) {
        loadTable();
    }

    private void loadTable(){
        try {
            List<Doctor> doctorList = doctorService.getAll(); // Changed from getAllPatient to getAll
            ObservableList<Doctor> doctorObservableList = FXCollections.observableArrayList();
            doctorObservableList.addAll(doctorList);
            tblDoctors.setItems(doctorObservableList);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }

    public void btnSearchOnAction(ActionEvent event) {
        String searchName = txtName.getText();

        if (searchName == null || searchName.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a name to search").showAndWait();
            return;
        }

        try{
            Doctor foundDoctor = doctorService.searchByName(searchName);

            if(foundDoctor!=null){
                populateFields(foundDoctor);
            } else {
                // Clear fields if no patient found
                new Alert(Alert.AlertType.ERROR, "Not Found! ").showAndWait();
//                txtName.setText("");
//                Combospecial.setValue(null);
//                ComboWeekDays.setValue(null);
//                txtQualifications.setText("");
//                txtQualifications.setText("");
//                originalName = "";
                clearForm();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void btnUpdateOnAction(ActionEvent event) {
        try {
            Doctor doctor = createDoctorFromForm();
            boolean success = doctorService.update(doctor, originalName);

            if(success) {
                new Alert( Alert.AlertType.INFORMATION,"Patient updated successfully!").showAndWait();

                originalName = txtName.getText(); // Update the original name
            } else {
                new Alert( Alert.AlertType.ERROR,"Failed to update patient").showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed updating ");
        }
    }

    public void btnDeleteOnAtion(ActionEvent event) throws SQLException {
        String name =  txtName.getText();

        doctorService.delete(name);
    }

    private void populateFields(Doctor doctor){

        if(doctor != null){
            txtName.setText(doctor.getName());
            Combospecial.setValue(doctor.getSpeciality());
            ComboWeekDays.setValue(doctor.getAvailability());
            txtQualifications.setText(doctor.getQualifications());
            txtContact.setText(doctor.getContact());

            if(doctor.getAvailableTime() != null  && !doctor.getAvailableTime().isEmpty()){
                parseAndSetTime(doctor.getAvailableTime());
            }
            originalName = doctor.getName();
        }

    }

    private boolean validateInput(){


        //validate name
        if(txtName.getText() == null || txtName.getText().trim().isEmpty()){
            new Alert( Alert.AlertType.ERROR,"NAme is required").showAndWait();
            return false;
        }

        //validate Age
        try{
            if (Combospecial.getValue() != null) {
                String selectedSpecialty = (String) Combospecial.getValue();
                // Do something with the value
            } else {
                System.out.println("Please select a specialty.");
            }
        }catch (NumberFormatException e){
            new Alert( Alert.AlertType.ERROR,"ERROR OCCURED").showAndWait();
            return false;
        }

        //validate  contact information

        if(txtContact.getText()==null || txtContact.getText().trim().isEmpty()){
            new Alert( Alert.AlertType.ERROR,"Contact information is required").showAndWait();
            return false;
        }

        return true;
    }


    private void clearForm() {
        txtName.setText("");
        Combospecial.setValue(null);
        ComboWeekDays.setValue(null);
        txtQualifications.setText("");
        txtContact.setText("");

        if(startTime.getValueFactory() != null){
            startTime.getValueFactory().setValue(1);
        }

        StartMeridiem.setValue(null);

        if(endTime.getValueFactory() != null){
            endTime.getValueFactory().setValue(1);
        }

        endMeridiem.setValue(null);

        originalName = "";
    }


    private String formatTime() {
        if (startTime.getValue() != null && StartMeridiem.getValue() != null &&
                endTime.getValue() != null && endMeridiem.getValue() != null) {

            // Format with consistent spacing and use dash as separator
            return String.format("%02d:00 %s - %02d:00 %s",
                    (Integer) startTime.getValue(),
                    (String) StartMeridiem.getValue(),
                    (Integer) endTime.getValue(),
                    (String) endMeridiem.getValue());
        }
        return ""; // Return empty string if time is not set
    }

    private void parseAndSetTime(String timeString) {
        // Parse time string format: "09:00 AM - 05:00 PM"
        try {
            String[] timeParts = timeString.split(" - ");
            if (timeParts.length == 2) {
                // Parse start time
                String[] startParts = timeParts[0].split(" ");
                if (startParts.length == 2) {
                    String[] startHourMin = startParts[0].split(":");
                    if (startHourMin.length == 2) {
                        int startHour = Integer.parseInt(startHourMin[0]);
                        startTime.getValueFactory().setValue(startHour);
                        StartMeridiem.setValue(startParts[1]);
                    }
                }

                // Parse end time
                String[] endParts = timeParts[1].split(" ");
                if (endParts.length == 2) {
                    String[] endHourMin = endParts[0].split(":");
                    if (endHourMin.length == 2) {
                        int endHour = Integer.parseInt(endHourMin[0]);
                        endTime.getValueFactory().setValue(endHour);
                        endMeridiem.setValue(endParts[1]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing time: " + e.getMessage());
        }
    }

    private Doctor createDoctorFromForm() {

        String formattedTime = formatTime();
        Doctor doctor =  new Doctor(
                0,// ID will be auto-generated by database
                txtName.getText(),
                (String) Combospecial.getValue(),
                (String) ComboWeekDays.getValue(),
                txtQualifications.getText(),
                txtContact.getText(),
                formattedTime
        );

        doctor.setAvailableTime(formattedTime);

        return doctor;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Combospecial.getItems().addAll( "Cardiologist",
                "Dermatologist",
                "Neurologist",
                "Pediatrician",
                "Psychiatrist",
                "Oncologist",
                "Orthopedic Surgeon",
                "General Physician",
                "Ophthalmologist",
                "ENT Specialist",
                "Gastroenterologist",
                "Endocrinologist",
                "Pulmonologist",
                "Nephrologist",
                "Urologist",
                "Rheumatologist",
                "Gynecologist",
                "Anesthesiologist",
                "Pathologist",
                "Radiologist");

        ComboWeekDays.getItems().addAll( "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday");

        StartMeridiem.getItems().addAll("AM","PM");

        StartMeridiem.setPrefWidth(70);
        StartMeridiem.setMaxWidth(70);

        endMeridiem.getItems().addAll("AM","PM");

        endMeridiem.setPrefWidth(70);
        endMeridiem.setMaxWidth(70);

        SpinnerValueFactory<Integer> startValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        startTime.setValueFactory(startValueFactory);
        //startTime.setPrefWidth(70);

        SpinnerValueFactory<Integer> endValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        endTime.setValueFactory(endValueFactory);
        //endTime.setPrefWidth(70);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSpeciality.setCellValueFactory(new PropertyValueFactory<>("speciality"));
        colAvailableDay.setCellValueFactory(new PropertyValueFactory<>("availability"));
    //    colAvailableTime.setCellValueFactory(new PropertyValueFactory<>("availableTime"));
        colQualifications.setCellValueFactory(new PropertyValueFactory<>("qualifications"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));

        loadTable();

    }



}
