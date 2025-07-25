package repository.custom;

import model.dao.PatientDao;
import repository.CRUDRepository;

import java.sql.SQLException;
import java.util.List;

public interface PatientRepository extends CRUDRepository<PatientDao,String> {


    boolean update(PatientDao dao, String originalName) throws SQLException;
}
