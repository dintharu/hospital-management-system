package controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class loginController {


    public JFXTextField txtUserName;
    public JFXTextField txtPassword;



    public void btnSignInOnAction(ActionEvent event) throws IOException {

        String username = txtUserName.getText().trim();
        String passwordText = txtPassword.getText().trim();

        if(username.isEmpty()||passwordText.isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Please fill the form").showAndWait();
            return;
        }

        int password;
        try {
            password = Integer.parseInt(passwordText);
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR,"Please enter the numebr correctly").showAndWait();
            txtPassword.clear();
            return;
        }
        if(username.contains("@gmail.com")&& password==1234 ){

            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBoard.fxml"))));
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

            stage.setResizable(false);
            stage.show();

        }else{
            new Alert(Alert.AlertType.ERROR,"The username is incorrect").showAndWait();
            txtUserName.clear();
            txtPassword.clear();
        }


    }


}
