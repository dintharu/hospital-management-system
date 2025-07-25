package repository.custom;

import model.dao.AppointmentDao;
import model.dao.PrescriptionDao;
import repository.CRUDRepository;

import java.sql.SQLException;
import java.util.List;

public interface PrescriptionRepository extends CRUDRepository<PrescriptionDao,String> {

    boolean add(PrescriptionDao dao) throws SQLException;

    boolean delete(String name) throws SQLException;

    PrescriptionDao searchByName(String patientName) throws SQLException;

    List<PrescriptionDao> getAll() throws SQLException;

    boolean update(PrescriptionDao prescriptionDao) throws SQLException;
}
