package repository.custom.Impl;

import DataBase.DBConnection;
import model.dao.BillDao;
import model.dao.PrescriptionDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import repository.custom.BillingRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BillingRepositoryImpl implements BillingRepository {
    Connection connection = DBConnection.getInstance ().getConnection();

    public BillingRepositoryImpl() throws SQLException {
    }

    @Override
    public boolean add(BillDao billDao) throws SQLException {
        String sql = "INSERT INTO billing(patient_id,total_amount,payment_status,generated_date)VALUES(?,?,?,?)";

        int patientId = getPatientIdByName(billDao.getPatientName());
        if (patientId == -1) {
            throw new SQLException("Patient not found: " + billDao.getPatientName());
        }

        QueryRunner runner = new QueryRunner();
        int rowAffected = runner.update(connection,sql,patientId,billDao.getTotalAmount(),billDao.getPaymentStatus(),billDao.getDate());
        return rowAffected>0;
    }

    @Override
    public boolean delete(String name) throws SQLException {
        QueryRunner runner = new QueryRunner();
        //  Delete appointment by patient name using JOIN
        String sql = "DELETE a FROM prescription a " +  //delete the records from appointment table
                "JOIN patient p ON a.patient_id = p.patient_id " +  //connect appointment table with the patient table from the foreighn key patient_id
                "WHERE p.name = ?"; // now its search the relevant name which we entered and find the relevant patient_id for that

        int rowAffected = runner.update(connection,sql,name);
        return rowAffected > 0;
    }

    @Override
    public BillDao searchByName(String name) throws SQLException {
        // First get the patient ID by name
        int patientId = getPatientIdByName(name);
        if (patientId == -1) {
            return null; // Patient not found
        }

        // Now search for prescription using patient_id and join with doctor table to get doctor name
        String sql = "SELECT b.bill_id, pt.name as patient_name, " +
                "b.total_amount, b.payment_status, b.generated_date " +
                "FROM billing b " +
                "JOIN patient pt ON p.patient_id = pt.patient_id " +
                "WHERE pt.name = ? " +
                "ORDER BY b.bill_id DESC LIMIT 1"; // Get the most recent prescription

        QueryRunner runner = new QueryRunner();
        try {
            // Using custom ResultSetHandler to handle the result
            ResultSetHandler<BillDao> resultSetHandler = new ResultSetHandler<BillDao>() {
                @Override
                public BillDao handle(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        return new BillDao(
                                resultSet.getInt("bill_id"),
                                resultSet.getString("patient_name"),
                                resultSet.getInt("total_amount"),
                                resultSet.getString("payment_status"),
                                resultSet.getDate("generated_date").toLocalDate()
                        );
                    }
                    return null;
                }
            };

            // Execute query using DbUtils
            return runner.query(connection, sql, resultSetHandler, name);

        } catch (SQLException e) {
            throw new SQLException("Error searching prescription by patient name: " + name, e);
        }
    }

    @Override
    public List<BillDao> getAll() throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql  = "SELECT b.bill_id, pt.name as patient_name, " +
                "b.total_amount, b.payment_status, b.generated_date " +
                "FROM billing b " +
                "JOIN patient pt ON b.patient_id = pt.patient_id" +
                " ORDER BY b.bill_id"; // Get all bills ordered by bill_id

        ResultSetHandler<List<BillDao>> resultSetHandler = rs -> {
            List<BillDao>billDaoList = new ArrayList<>();

            while (rs.next()) {
                BillDao billDao = new BillDao();
                billDao.setBillId(rs.getInt("bill_id"));
                billDao.setPatientName(rs.getString("patient_name"));
                billDao.setTotalAmount(rs.getInt("total_amount"));
                billDao.setPaymentStatus(rs.getString("payment_status"));
                billDao.setDate(rs.getDate("generated_date").toLocalDate());
            }
            return billDaoList;
        };

        return runner.query(connection,sql,resultSetHandler);

    }

    @Override
    public boolean update(BillDao billDao) throws SQLException {
        // Get patient ID by name
        int patientId = getPatientIdByName(billDao.getPatientName());
        if (patientId == -1) {
            throw new SQLException("Patient not found: " + billDao.getPatientName());
        }

        QueryRunner runner = new QueryRunner();
        String sql = "UPDATE billing SET patient_id = ?,total_amount = ?, payment_status = ?, generated_date = ?,  WHERE bill_id = ?";

        int rowsAffected = runner.update(connection, sql,
                patientId,
                billDao.getTotalAmount(),
                billDao.getPaymentStatus(),
                billDao.getDate(),
                billDao.getBillId()
        );

        return rowsAffected > 0;
    }

    private int getPatientIdByName(String patientName) throws SQLException {

        QueryRunner runner = new QueryRunner();

        String sql = "SELECT patient_id FROM patient WHERE name = ?";

        Integer patient_id = runner.query(connection,sql, new ScalarHandler<>(),patientName);
        return patient_id != null ? patient_id : -1;

    }
}
