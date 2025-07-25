package controller;

import com.jfoenix.controls.JFXComboBox;
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
import model.dto.Prescription;
import model.dto.RoomAllocation;
import org.controlsfx.control.Notifications;
import service.ServiceFactory;
import service.custom.DoctorService;
import service.custom.PatientService;
import service.custom.RoomAllocationService;
import service.custom.RoomService;
import util.ServiceType;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class RoomAllocationController implements Initializable {
    public JFXComboBox PatientCombo;
    public JFXTextField txtRoomNumber;
    public DatePicker AdmitDate;
    public DatePicker DischargeDate;
    public JFXComboBox comboStatus;
    public TextArea txtNotes;
    public JFXComboBox DoctorCombo;
    public TextField txtTotalCost;
    public TextField txtTotalDays;
    public TableView tblRoomAllocation;
    public TableColumn colAllocationId;
    public TableColumn colPatientName;
    public TableColumn colRoomNumber;
    public TableColumn colAdmittedDate;
    public TableColumn colDischargeDate;
    public TableColumn colStatus;
    public TableColumn colNotes;
    public TableColumn colAllocatorName;
    public TableColumn colTotalDays;
    public TableColumn colTotalCost;
    public JFXComboBox ComboRoomNumber;

    PatientService patientService = ServiceFactory.getInstance().getServiceType(ServiceType.PATIENT);

    DoctorService doctorService = ServiceFactory.getInstance().getServiceType(ServiceType.DOCTOR);

    RoomAllocationService roomAllocationService = ServiceFactory.getInstance().getServiceType(ServiceType.ROOMALLOCATION);

    RoomService  roomService = ServiceFactory.getInstance().getServiceType(ServiceType.ROOM);

    public RoomAllocationController() throws SQLException {
    }

    private void loadPatientNames(){
        try {
            List<String> patientNames = patientService.getPatientNames();
            PatientCombo.setItems(FXCollections.observableArrayList(patientNames));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDoctorNames(){
        try {
            List<String> doctorNames = doctorService.getDoctorNames();
            DoctorCombo.setItems(FXCollections.observableArrayList(doctorNames));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadRoomNumbers() throws Exception {
        try {
            List<Integer> roomNumbers = roomService.getRoomNumbers();
            ComboRoomNumber.setItems(FXCollections.observableArrayList(roomNumbers));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void btnOnAddAction(ActionEvent event) {
        if(validateInput()){
            try {
                RoomAllocation roomAllocation = createRoomAllocationForm();
                roomAllocationService.add(roomAllocation);

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

    public void btnOnUpdateAction(ActionEvent event) {
        try {

            RoomAllocation roomAllocation = createRoomAllocationForm();
            boolean success = roomAllocationService.update(roomAllocation);

            if(success) {
                new Alert( Alert.AlertType.INFORMATION,"Patient updated successfully!").showAndWait();

            } else {
                new Alert( Alert.AlertType.ERROR,"Failed to update patient").showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed updating ");
        }
    }

    public void btnOnSearchAction(ActionEvent event) {
        try {
            // Get selected patient name
            String selectedPatientName = (String) PatientCombo.getValue();

            if (selectedPatientName == null || selectedPatientName.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please select a patient to search").showAndWait();
                return;
            }

            // Search for appointment by patient name
            RoomAllocation roomAllocation = roomAllocationService.searchByName(selectedPatientName);

            if (roomAllocation != null) {
                // Set the form values with the found appointment data
                populateFields(roomAllocation);

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

    public void btnOnDeleteAction(ActionEvent event) throws SQLException {
        String name = PatientCombo.getValue().toString();

        patientService.delete(name);
    }

    public void btnOnActionReload(ActionEvent event) {
        loadTable();
    }

    private void populateFields(RoomAllocation roomAllocation){

        if(roomAllocation != null){
            roomAllocatoonid  = roomAllocation.getAllocationId();
            PatientCombo.setValue(roomAllocation.getPatinetName());
            ComboRoomNumber.setValue(roomAllocation.getRoomNumber());
            AdmitDate.setValue(roomAllocation.getAdmissionDate());
            DischargeDate.setValue(roomAllocation.getDischargeDate());
            comboStatus.setValue(roomAllocation.getStatus());
            txtNotes.setText(roomAllocation.getNotes());
            DoctorCombo.setValue(roomAllocation.getAllocatedBy());
            txtTotalDays.setText(String.valueOf(roomAllocation.getTotalDays()));
            txtTotalCost.setText(String.valueOf(roomAllocation.getTotalCost()));


        }
    }

    private boolean validateInput(){


        //validate name
        if(txtNotes.getText() == null || txtTotalCost.getText().trim().isEmpty()){
            new Alert( Alert.AlertType.ERROR,"medicine and dosage  is not filles").showAndWait();
            return false;
        }

        return true;
    }

    private  Integer roomAllocatoonid = 0;
    private void clearForm() {
        PatientCombo.setValue(null);
        DoctorCombo.setValue(null);
        ComboRoomNumber.setValue(null);
        AdmitDate.setValue(null);
        DischargeDate.setValue(null);
        comboStatus.setValue(null);
        txtNotes.clear();
        txtTotalCost.clear();
        txtTotalDays.clear();

        roomAllocatoonid = 0;
    }




    private RoomAllocation createRoomAllocationForm() {
        RoomAllocation roomAllocation =  new RoomAllocation(
                roomAllocatoonid,
                PatientCombo.getValue().toString(),
                (Integer) ComboRoomNumber.getValue(),
                AdmitDate.getValue(),
                DischargeDate.getValue(),
                comboStatus.getValue().toString(),
                txtNotes.getText(),
                DoctorCombo.getValue().toString(),
                Integer.parseInt(txtTotalDays.getText()),
                Double.parseDouble(txtTotalCost.getText())
        );
        return roomAllocation;
    }

    private void loadTable(){
        try {
            List<RoomAllocation> roomAllocationsList = roomAllocationService.getAll(); // Changed from getAllPatient to getAll
            ObservableList<RoomAllocation> roomAllocationObservableList = FXCollections.observableArrayList();


            roomAllocationObservableList.addAll(roomAllocationsList);

            tblRoomAllocation.setItems(roomAllocationObservableList);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDoctorNames();
        loadPatientNames();
        loadTable();
        try {
            loadRoomNumbers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        comboStatus.getItems().addAll("Admitted", "Discharged");

        colAllocationId.setCellValueFactory(new PropertyValueFactory<>("allocationId"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patinetName"));
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colAdmittedDate.setCellValueFactory(new PropertyValueFactory<>("admissionDate"));
        colDischargeDate.setCellValueFactory(new PropertyValueFactory<>("dischargeDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        colAllocatorName.setCellValueFactory(new PropertyValueFactory<>("allocatedBy"));
        colTotalDays.setCellValueFactory(new PropertyValueFactory<>("totalDays"));
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
    }
}
