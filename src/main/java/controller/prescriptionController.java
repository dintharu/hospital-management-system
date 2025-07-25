package controller;

import DataBase.DBConnection;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import model.dao.PrescriptionDao;
import model.dto.Appointment;
import model.dto.Doctor;
import model.dto.Prescription;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.Notifications;
import service.ServiceFactory;
import service.custom.DoctorService;
import service.custom.Impl.PatientServiceImpl;
import service.custom.PatientService;
import service.custom.PrescriptionService;
import util.ServiceType;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class prescriptionController implements Initializable {

    public JFXComboBox PatientCombo;
    public JFXComboBox DoctorCombo;
    public TextArea txtAdvice;
    public TableView tblPrescription;
    public TableColumn colPresciptionId;
    public TableColumn colPatientName;
    public TableColumn colDoctorName;
    public TableColumn colMedicine;
    public TableColumn colDosage;
    public TableColumn colDuration;
    public TableColumn colAdvices;
    public JFXTextField txtMedicine;
    public JFXTextField txtDosage;
    public JFXTextField txtDuration;
    PatientService patientService = ServiceFactory.getInstance().getServiceType(ServiceType.PATIENT);
    DoctorService doctorService = ServiceFactory.getInstance().getServiceType(ServiceType.DOCTOR);

    PrescriptionService prescriptionService = ServiceFactory.getInstance().getServiceType(ServiceType.PRESCRIPTION);
    public prescriptionController() throws SQLException {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDoctorNames();
        loadPatientNames();

        colPresciptionId.setCellValueFactory(new PropertyValueFactory<>("prescription_id"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colMedicine.setCellValueFactory(new PropertyValueFactory<>("medicine"));
        colDosage.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        loadTable();


    }

    public void btnOnAddAction(ActionEvent event) {
        if(validateInput()){
            try {
                Prescription prescription = createPrescriptiomFromForm();

                prescriptionService.add(prescription);

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


    private int currentPrescriptionId = 0;

    public void btnOnUpdateAction(ActionEvent event) {
        try {

            Prescription prescription = createPrescriptiomFromForm();
            boolean success = prescriptionService.update(prescription);

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
            Prescription prescription = prescriptionService.searchByName(selectedPatientName);

            if (prescription != null) {
                // Set the form values with the found appointment data
                populateFields(prescription);

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

        prescriptionService.delete(name);
    }

    public void btnOnBillsAction(ActionEvent event) {

        try {
            JasperDesign design = JRXmlLoader.load("src/main/resources/report/Prescription.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(design);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, DBConnection.getInstance().getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint,"Patient_Prescription.pdf");
            JasperViewer.viewReport(jasperPrint,false);
        } catch (JRException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //-----------------------------------validation and extra   methods for efficiency

    private void populateFields(Prescription prescription){

        if(prescription != null){
            currentPrescriptionId  = prescription.getPrescription_id();
            PatientCombo.setValue(prescription.getPatientName());
            DoctorCombo.setValue(prescription.getDoctorName());
            txtMedicine.setText(prescription.getMedicine());
            txtDosage.setText(prescription.getDosage());
            txtDuration.setText(prescription.getDuration());


        }

    }

    private boolean validateInput(){


        //validate name
        if(txtMedicine.getText() == null || txtDosage.getText().trim().isEmpty()){
            new Alert( Alert.AlertType.ERROR,"medicine and dosage  is not filles").showAndWait();
            return false;
        }

        //validate Age
//        try{
//            if (PatientCombo.getValue() != null) {
//                String selectedSpecialty = (String) Combospecial.getValue();
//                // Do something with the value
//            } else {
//                System.out.println("Please select a specialty.");
//            }
//        }catch (NumberFormatException e){
//            new Alert( Alert.AlertType.ERROR,"ERROR OCCURED").showAndWait();
//            return false;
//        }

        //validate  contact information

//        if(txtContact.getText()==null || txtContact.getText().trim().isEmpty()){
//            new Alert( Alert.AlertType.ERROR,"Contact information is required").showAndWait();
//            return false;
//        }

        return true;
    }


    private void loadTable(){
        try {
            List<Prescription> prescriptionList = prescriptionService.getAll(); // Changed from getAllPatient to getAll
            ObservableList<Prescription> prescriptionObservableList = FXCollections.observableArrayList();


            prescriptionObservableList.addAll(prescriptionList);

            tblPrescription.setItems(prescriptionObservableList);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }



    private void clearForm() {
        currentPrescriptionId = 0;
        PatientCombo.setValue(null);
        DoctorCombo.setValue(null);
        txtMedicine.setText("");
        txtDosage.setText("");
        txtDuration.setText("");
    }




    private Prescription createPrescriptiomFromForm() {

        Prescription prescription =  new Prescription(
                currentPrescriptionId,// ID will be auto-generated by database
                (String) PatientCombo.getValue(),
                (String) DoctorCombo.getValue(),
                txtMedicine.getText(),
                txtDosage.getText(),
                txtDuration.getText()
        );

      return prescription;
    }

    public void btnOnActionReload(ActionEvent event) {
        loadTable();
    }
}
