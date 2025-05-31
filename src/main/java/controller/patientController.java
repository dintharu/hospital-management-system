package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Patient;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class patientController implements Initializable {
    public JFXTextField txtName;
    public JFXTextField txtAge;
    public JFXTextField txtContact;
    public JFXTextField txtImgContact;
    public JFXTextArea txtMedicalHistory;
    public JFXComboBox ComboGender;
    public TableView tblPatients;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colGender;
    public TableColumn colContact;
    public TableColumn colImgContact;
    public TableColumn colMedicalHis;
    public TableColumn colAge;


    ArrayList<Patient>patientArrayList = new ArrayList<>();

    private  int nextId = 1;
    public void btnAddOnAction(ActionEvent event) {

        String name = txtName.getText();
        int age = Integer.parseInt(txtAge.getText());
        String gender = (String) ComboGender.getValue();
        int contact = Integer.parseInt(txtContact.getText());
        int imergencyContact = Integer.parseInt(txtImgContact.getText());
        String medicalHistory = txtMedicalHistory.getText();

        Patient patient = new Patient(nextId,name,age,gender,contact,imergencyContact,medicalHistory);
        patientArrayList.add(patient);
        nextId++;


    }

    public void btnReloadOnAction(ActionEvent event) {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colImgContact.setCellValueFactory(new PropertyValueFactory<>("imergencyContact"));
        colMedicalHis.setCellValueFactory(new PropertyValueFactory<>("medicalHistory"));


        ObservableList<Patient> patientObservableList = FXCollections.observableArrayList();

        patientArrayList.forEach(patient -> {
            patientObservableList.add(patient);
        });

        tblPatients.setItems(patientObservableList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ComboGender.getItems().addAll("Male","Female","Other");
    }
}
