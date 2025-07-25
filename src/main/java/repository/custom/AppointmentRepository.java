package repository.custom;

import model.dao.AppointmentDao;
import model.dao.DoctorDao;
import model.dto.Appointment;
import repository.CRUDRepository;

import java.sql.SQLException;
import java.util.List;

public interface AppointmentRepository extends CRUDRepository<AppointmentDao,String> {

    boolean add(AppointmentDao dao) throws SQLException;

    boolean delete(String name) throws SQLException;

    AppointmentDao searchByName(String patientName) throws SQLException;

    List<AppointmentDao> getAll() throws SQLException;

    boolean update(AppointmentDao appointmentDao) throws SQLException;
}
