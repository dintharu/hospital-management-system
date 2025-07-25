package repository.custom;

import model.dao.BillDao;
import model.dao.PatientDao;
import repository.CRUDRepository;

import java.sql.SQLException;
import java.util.List;

public interface BillingRepository extends CRUDRepository<BillDao,String> {

    boolean add(BillDao billDao) throws SQLException;

    boolean delete(String name) throws SQLException;

    BillDao searchByName(String name) throws SQLException;

    List<BillDao> getAll() throws SQLException;

    boolean update(BillDao billDao
    ) throws SQLException;
}
