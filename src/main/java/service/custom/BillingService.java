package service.custom;

import model.dto.Bill;
import model.dto.Patient;
import service.SuperService;

import java.sql.SQLException;
import java.util.List;

public interface BillingService extends SuperService {

    boolean add(Bill  bill) throws SQLException;

    List<Bill> getAll() throws SQLException;

    boolean delete(String name) throws SQLException;

    Bill searchByName(String name) throws SQLException;

    boolean update(Bill bill) throws SQLException;

//    List<String> getPatientNames() throws SQLException;

}
