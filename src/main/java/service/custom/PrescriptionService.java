package service.custom;

import model.dto.Appointment;
import model.dto.Prescription;
import service.SuperService;

import java.sql.SQLException;
import java.util.List;

public interface PrescriptionService extends SuperService {
    boolean add(Prescription prescription) throws SQLException;

    List<Prescription> getAll() throws SQLException;

    boolean delete(String name) throws SQLException;

    Prescription searchByName(String patientName) throws SQLException;

    boolean update(Prescription prescription) throws SQLException;
}
