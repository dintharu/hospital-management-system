package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTimePicker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import model.dto.Appointment;
import model.dto.Doctor;
import model.dto.Patient;
import org.controlsfx.control.Notifications;
import service.ServiceFactory;
import service.custom.AppointmentService;
import service.custom.DoctorService;
import service.custom.EmailService;
import service.custom.Impl.DoctorServiceImpl;
import service.custom.PatientService;
import util.ServiceType;

import javax.print.Doc;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class appointmentController implements Initializable {

    public ComboBox patientCombo;
    public ComboBox doctorCombo;
    public TableView tblAppointments;
    public TableColumn colId;
    public TableColumn colPatientName;
    public TableColumn colDoctorName;
    public TableColumn colDate;
    public TableColumn colTime;
    public Spinner startTime;
    public JFXComboBox StartMeridiem;
    public Spinner endTime;
    public JFXComboBox endMeridiem;
    public JFXComboBox ComboWeekDays;


    PatientService patientService = ServiceFactory.getInstance().getServiceType(ServiceType.PATIENT);

    DoctorService doctorService = ServiceFactory.getInstance().getServiceType(ServiceType.DOCTOR);


    private final AppointmentService appointmentService;

    public appointmentController() throws SQLException {
        try {
            this.appointmentService = ServiceFactory.getInstance().getServiceType(ServiceType.APPOINTMENT);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize PatientService", e);
        }
    }


    public void btnOnActionBook(ActionEvent event) {

        if(validateInput()){
            try {
                Appointment appointment = createAppointmentFromForm();
                appointmentService.add(appointment);

                // Get patient details for email
                String patientName = (String) patientCombo.getValue();
                Patient patient = patientService.searchByName(patientName);

                // Send email notification
                if (patient != null && isValidEmail(patient.getContact())) {
                    EmailService emailService = ServiceFactory.getInstance().getServiceType(ServiceType.EMAIL);
                    boolean emailSent = emailService.sendAppointmentConfirmation(patient, appointment);

                    if (emailSent) {
                        System.out.println("Email confirmation sent to: " + patient.getContact());
                    } else {
                        System.out.println("Failed to send email confirmation");
                    }
                } else {
                    System.out.println("Invalid email address or patient not found");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            clearForm();
            Notifications notificationBuilder = Notifications.create()
                    .title("Appointment Booked Successfully")
                    .text("Confirmation email sent to patient")
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

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Simple email validation regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private void setAppointmentDataToForm(Appointment appointment) {
        try {
            // Set patient name (already selected)
            patientCombo.setValue(appointment.getPatientName());

            // Set doctor name
            doctorCombo.setValue(appointment.getDoctorName());

            // Set appointment date/day
            if (appointment.getDate() != null && !appointment.getDate().isEmpty()) {
                ComboWeekDays.setValue(appointment.getDate());
            }

            // Set appointment time
            if (appointment.getTime() != null && !appointment.getTime().isEmpty()) {
                parseAndSetTime(appointment.getTime());
            }

        } catch (Exception e) {
            System.out.println("Error setting appointment data to form: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR,
                    "Error loading appointment data: " + e.getMessage()).showAndWait();
        }
    }

    public void btnOnActionSearch(ActionEvent event) {
        try {
            // Get selected patient name
            String selectedPatientName = (String) patientCombo.getValue();

            if (selectedPatientName == null || selectedPatientName.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please select a patient to search").showAndWait();
                return;
            }

            // Search for appointment by patient name
            Appointment appointment = appointmentService.searchByName(selectedPatientName);

            if (appointment != null) {
                // Set the form values with the found appointment data
                setAppointmentDataToForm(appointment);

                // Show success notification
                Notifications.create()
                        .title("Appointment Found")
                        .text("Appointment details loaded successfully")
                        .graphic(null)
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.BOTTOM_RIGHT)
                        .showInformation();
            } else {
                // Show not found message
                new Alert(Alert.AlertType.INFORMATION,
                        "No appointment found for patient: " + selectedPatientName).showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Database error occurred while searching: " + e.getMessage()).showAndWait();
        }

    }

    public void btnOnActionUpdate(ActionEvent event) {
        try {
            // Get selected patient name
            String selectedPatientName = (String) patientCombo.getValue();

            if (selectedPatientName == null || selectedPatientName.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please select a patient to update appointment").showAndWait();
                return;
            }

            // Validate that all fields are filled
            if (!validateInput()) {
                new Alert(Alert.AlertType.WARNING, "Please fill in all required fields").showAndWait();
                return;
            }

            Appointment existingAppointment = appointmentService.searchByName(selectedPatientName);
            if (existingAppointment == null) {
                new Alert(Alert.AlertType.WARNING,
                        "No appointment found for patient: " + selectedPatientName).showAndWait();
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Update");
            confirmAlert.setHeaderText("Update Appointment");
            confirmAlert.setContentText("Are you sure you want to update the appointment for " + selectedPatientName + "?");

            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                Appointment updatedAppointment = createAppointmentFromForm();
                boolean updated = appointmentService.update(updatedAppointment);

                if (updated) {
                    // Send update confirmation email
                    Patient patient = patientService.searchByName(selectedPatientName);
                    if (patient != null && isValidEmail(patient.getContact())) {
                        EmailService emailService = ServiceFactory.getInstance().getServiceType(ServiceType.EMAIL);
                        boolean emailSent = emailService.sendAppointmentUpdate(patient, updatedAppointment);

                        if (emailSent) {
                            System.out.println("Update confirmation email sent to: " + patient.getContact());
                        } else {
                            System.out.println("Failed to send update confirmation email");
                        }
                    }

                    clearForm();
                    Notifications.create()
                            .title("Appointment Updated")
                            .text("Appointment for " + selectedPatientName + " has been updated successfully")
                            .graphic(null)
                            .hideAfter(Duration.seconds(5))
                            .position(Pos.BOTTOM_RIGHT)
                            .showInformation();
                } else {
                    new Alert(Alert.AlertType.ERROR,
                            "Failed to update appointment. Please try again.").showAndWait();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Database error occurred while updating appointment: " + e.getMessage()).showAndWait();
        }
    }

    public void btnOnActionCancel(ActionEvent event) throws SQLException {
        try {
            String patientName = (String) patientCombo.getValue();

            // Check if patient name is selected
            if (patientName == null || patientName.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please select a patient to cancel appointment").showAndWait();
                return;
            }

            // Get appointment details before deletion for email
            Appointment appointmentToCancel = appointmentService.searchByName(patientName);
            if (appointmentToCancel == null) {
                new Alert(Alert.AlertType.INFORMATION,
                        "No appointment found for " + patientName).showAndWait();
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Cancellation");
            confirmAlert.setHeaderText("Cancel Appointment");
            confirmAlert.setContentText("Are you sure you want to cancel the appointment for " + patientName + "?");

            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                boolean deleted = appointmentService.delete(patientName);

                if (deleted) {
                    // Send cancellation confirmation email
                    Patient patient = patientService.searchByName(patientName);
                    if (patient != null && isValidEmail(patient.getContact())) {
                        EmailService emailService = ServiceFactory.getInstance().getServiceType(ServiceType.EMAIL);
                        boolean emailSent = emailService.sendAppointmentCancel(patient, appointmentToCancel);

                        if (emailSent) {
                            System.out.println("Cancellation confirmation email sent to: " + patient.getContact());
                        } else {
                            System.out.println("Failed to send cancellation confirmation email");
                        }
                    }

                    clearForm();
                    Notifications.create()
                            .title("Appointment Cancelled")
                            .text("Appointment for " + patientName + " has been cancelled successfully")
                            .graphic(null)
                            .hideAfter(Duration.seconds(5))
                            .position(Pos.BOTTOM_RIGHT)
                            .showInformation();
                } else {
                    new Alert(Alert.AlertType.INFORMATION,
                            "No appointment found for " + patientName).showAndWait();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Database error occurred while cancelling appointment: " + e.getMessage()).showAndWait();
        }
    }

    public void btnOnActionReload(ActionEvent event) {
        System.out.println("reload button clicked ");
        loadTable();
    }

    private void loadTable(){
        try {
            List<Appointment> appointmentList = appointmentService.getAll(); // Changed from getAllPatient to getAll
            ObservableList<Appointment> appointmentObservableList = FXCollections.observableArrayList();


            appointmentObservableList.addAll(appointmentList);

            tblAppointments.setItems(appointmentObservableList);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }

    private void loadPatientNames(){
        try {
            List<String> patientNames = patientService.getPatientNames();
            patientCombo.setItems(FXCollections.observableArrayList(patientNames));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDoctorNames(){
        try {
            List<String> doctorNames = doctorService.getDoctorNames();
            doctorCombo.setItems(FXCollections.observableArrayList(doctorNames));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private void setDoctorAvailability(String doctorName) {
        try {
            Doctor doctor = doctorService.searchByName(doctorName);
            if (doctor != null) {
                // Set the available day in the combo box
                if (doctor.getAvailability() != null && !doctor.getAvailability().isEmpty()) {
                    ComboWeekDays.setValue(doctor.getAvailability());
                }

                // Set the available time
                if (doctor.getAvailableTime() != null && !doctor.getAvailableTime().isEmpty()) {
                    parseAndSetTime(doctor.getAvailableTime());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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


    private boolean validateInput(){


        //validate patient name

        try{
            if (patientCombo.getValue() != null) {
                String selectedPatientName = (String) patientCombo.getValue();
                // Do something with the value
            } else {
                System.out.println("Please select a pateint.");
            }
        }catch (NumberFormatException e){
            new Alert( Alert.AlertType.ERROR,"ERROR OCCURED").showAndWait();
            return false;
        }

        //validate doctor name

        try{
            if (doctorCombo.getValue() != null) {
                String selectedDoctorName = (String) doctorCombo.getValue();

            } else {
                System.out.println("Please select a doctor.");
            }
        }catch (NumberFormatException e){
            new Alert( Alert.AlertType.ERROR,"ERROR OCCURED").showAndWait();
            return false;
        }

        return true;
    }


    private void clearForm() {
        patientCombo.setValue(null);
        doctorCombo.setValue(null);
        ComboWeekDays.setValue(null);

        // Clear time spinners
        if (startTime.getValueFactory() != null) {
            startTime.getValueFactory().setValue(1);
        }
        if (endTime.getValueFactory() != null) {
            endTime.getValueFactory().setValue(1);
        }
        StartMeridiem.setValue(null);
        endMeridiem.setValue(null);

    }

    private Appointment createAppointmentFromForm() {
        return new Appointment(
                0,// ID will be auto-generated by database
                (String) patientCombo.getValue(),
                (String) doctorCombo.getValue(),
                (String) ComboWeekDays.getValue(),
                formatTime()
        );
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadPatientNames();
        loadDoctorNames();
        StartMeridiem.getItems().addAll("AM", "PM");
        StartMeridiem.setPrefWidth(70);
        StartMeridiem.setMaxWidth(70);
        endMeridiem.getItems().addAll("AM", "PM");
        endMeridiem.setPrefWidth(70);
        endMeridiem.setMaxWidth(70);

        ComboWeekDays.getItems().addAll( "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday");

        SpinnerValueFactory<Integer> startValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        startTime.setValueFactory(startValueFactory);

        SpinnerValueFactory<Integer> endValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        endTime.setValueFactory(endValueFactory);

        // Add listener to doctor combo box to automatically set availability
        doctorCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setDoctorAvailability((String) newValue);
            }
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        loadTable();
    }
}
