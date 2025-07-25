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
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import model.dao.RoomDao;
import model.dto.Prescription;
import model.dto.Room;
import org.controlsfx.control.Notifications;
import service.ServiceFactory;
import service.custom.RoomService;
import util.ServiceType;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class RoomManagementController implements Initializable {

    public JFXTextField txtRoomNumber;
    public JFXComboBox comboRoomType;
    public JFXTextField txtFloorNumber;
    public JFXTextField txtCapacity;
    public JFXTextField txtDailyRate;
    public JFXComboBox comboStatus;
    public JFXTextArea txtAmenities;
    public DatePicker datePicker;
    public TableView tblRooms;
    public TableColumn colRoomId;
    public TableColumn colRoomNumber;
    public TableColumn colRoomType;
    public TableColumn colCapacity;
    public TableColumn colFloorNumber;
    public TableColumn colDailyRate;
    public TableColumn colStatus;
    public TableColumn colAmenities;
    public TableColumn colDate;


    RoomService roomService = ServiceFactory.getInstance().getServiceType(ServiceType.ROOM);

    public RoomManagementController() throws SQLException {
    }

    public void btnOnAddAction(ActionEvent event) {
        if(validateInput()){
            try {
                Room room = createRoomForm();
                roomService.add(room);

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


    private void clearForm() {
        txtRoomNumber.clear();
        comboRoomType.getSelectionModel().clearSelection();
        txtFloorNumber.clear();
        txtCapacity.clear();
        txtDailyRate.clear();
        comboStatus.getSelectionModel().clearSelection();
        txtAmenities.clear();
        datePicker.setValue(null);
    }


    private boolean validateInput() {
        if (txtRoomNumber.getText().isEmpty() || comboRoomType.getValue() == null ||
                txtFloorNumber.getText().isEmpty() || txtCapacity.getText().isEmpty() ||
                txtDailyRate.getText().isEmpty() || comboStatus.getValue() == null ||
                txtAmenities.getText().isEmpty() || datePicker.getValue() == null) {
            Notifications.create()
                    .title("Validation Error")
                    .text("Please fill all fields")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_RIGHT)
                    .showError();
            return false;
        }
        return true;
    }

    private int currentRoomId = 0;
    private Room createRoomForm() {
        return new Room(
                currentRoomId,
                Integer.parseInt(txtRoomNumber.getText()),
                (String) comboRoomType.getValue(),
                Integer.parseInt(txtFloorNumber.getText()),
                Integer.parseInt(txtCapacity.getText()),
                Double.parseDouble(txtDailyRate.getText()),
                (String) comboStatus.getValue(),
                txtAmenities.getText(),
                datePicker.getValue()
        );
    }


    public void btnOnUpdateAction(ActionEvent event) {
        try {
            Room room = createRoomForm();
            boolean success = roomService.update(room,originalRoomNumber);

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
        Integer roomNumber = Integer.parseInt(txtRoomNumber.getText());

        if (roomNumber == null) {
            new Alert(Alert.AlertType.WARNING, "Please enter a number to search").showAndWait();
            return;
        }

        try{

            RoomDao foundRoom = roomService.searchById(roomNumber);
            if(foundRoom!=null){
                populateFields(foundRoom);
            } else {
                // Clear fields if no patient found
                new Alert(Alert.AlertType.ERROR, "Not Found! ").showAndWait();
                clearForm();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    Integer originalRoomNumber = 0;
    private void populateFields(RoomDao room) {

        if (room != null) {
            txtRoomNumber.setText(String.valueOf(room.getRoomNumber()));
            comboRoomType.setValue(room.getRoomType());
            txtFloorNumber.setText(String.valueOf(room.getFloorNumber()));
            txtCapacity.setText(String.valueOf(room.getCapacity()));
            txtDailyRate.setText(String.valueOf(room.getDailyRate()));
            comboStatus.setValue(room.getStatus());
            txtAmenities.setText(room.getAmenities());
            datePicker.setValue(room.getDate());
            originalRoomNumber = room.getRoomNumber();
        } else {
            new Alert(Alert.AlertType.ERROR, "No room found with the given number").showAndWait();
        }
    }

    public void btnOnDeleteAction(ActionEvent event) throws SQLException {

        Integer roomNumber = Integer.parseInt(txtRoomNumber.getText());
        roomService.delete(roomNumber);
    }

    public void btnOnActionReload(ActionEvent event) {
        loadTable();
    }

    private void loadTable(){
        try {
            List<RoomDao> roomList = roomService.getAll(); // Changed from getAllPatient to getAll
            ObservableList<RoomDao>roomDaoObservableList = FXCollections.observableArrayList();


            roomDaoObservableList.addAll(roomList);

            tblRooms.setItems(roomDaoObservableList);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboRoomType.getItems().addAll("General", "Private", "ICU", "Emergency");
        comboStatus.getItems().addAll("Available", "Occupied", "Maintenance");

        colRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colFloorNumber.setCellValueFactory(new PropertyValueFactory<>("floorNumber"));
        colDailyRate.setCellValueFactory(new PropertyValueFactory<>("dailyRate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAmenities.setCellValueFactory(new PropertyValueFactory<>("amenities"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        loadTable();
    }
}
