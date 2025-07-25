package service.custom;

import model.dto.Patient;
import service.SuperService;

import java.sql.SQLException;
import java.util.List;

public interface PatientService extends SuperService {
    boolean add(Patient patient) throws SQLException;

    List<Patient> getAll() throws SQLException;

    boolean delete(String name) throws SQLException;

    Patient searchByName(String name) throws SQLException;

    boolean update(Patient patient, String originalName) throws SQLException;

    List<String> getPatientNames() throws SQLException;
}
