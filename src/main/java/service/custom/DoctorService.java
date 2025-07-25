package service.custom;

import model.dto.Doctor;
import model.dto.Patient;
import service.SuperService;

import javax.print.Doc;
import java.sql.SQLException;
import java.util.List;

public interface DoctorService extends SuperService {

    boolean add(Doctor doctor) throws SQLException;

    List<Doctor> getAll() throws SQLException;

    boolean delete(String name) throws SQLException;

    Doctor searchByName(String name) throws SQLException;

    boolean update(Doctor doctor, String originalName) throws SQLException;

    List<String> getDoctorNames() throws SQLException;

}
