package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DashBoardController {
    public void btnDoctorOnAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/doctor.fxml"))));
        stage.show();

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        stage.setResizable(false);
        stage.show();
    }

    public void btnPatientOnAction(ActionEvent event) throws IOException {

        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/patientView.fxml"))));
        stage.show();

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        stage.setResizable(false);
        stage.show();
    }

    public void btnAdminOnAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/appointment.fxml"))));
        stage.show();

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        stage.setResizable(false);
        stage.show();
    }

    public void btnPrescriptionOnAction(ActionEvent event) throws IOException {

        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/prescription.fxml"))));
        stage.show();

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        stage.setResizable(false);
        stage.show();
    }

    public void btnBillViewOnAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/BillingView.fxml"))));
        stage.show();

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        stage.setResizable(false);
        stage.show();
    }

    public void btnRoomManagementOnAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/RoomManagement.fxml"))));
        stage.show();

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        stage.setResizable(false);
        stage.show();
    }

    public void btnRoomAllocationOnAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/RoomAllocation.fxml"))));
        stage.show();

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        stage.setResizable(false);
        stage.show();
    }
}
