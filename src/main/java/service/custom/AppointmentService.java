package service.custom;

import model.dto.Appointment;
import model.dto.Doctor;
import service.SuperService;

import java.sql.SQLException;
import java.util.List;

public interface AppointmentService extends SuperService {

    boolean add(Appointment appointment) throws SQLException;

    List<Appointment> getAll() throws SQLException;

    boolean delete(String name) throws SQLException;

    Appointment searchByName(String patientName) throws SQLException;

    boolean update(Appointment appointment) throws SQLException;


}
