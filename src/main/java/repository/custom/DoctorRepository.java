package repository.custom;

import model.dao.DoctorDao;
import model.dao.PatientDao;
import repository.CRUDRepository;

import java.sql.SQLException;
import java.util.List;

public interface DoctorRepository extends CRUDRepository<DoctorDao,String> {
    boolean add(DoctorDao dao) throws SQLException;

    boolean delete(String name) throws SQLException;

    DoctorDao searchByName(String name) throws SQLException;

    List<DoctorDao> getAll() throws SQLException;

    boolean update(DoctorDao dao, String originalName) throws SQLException;
}
