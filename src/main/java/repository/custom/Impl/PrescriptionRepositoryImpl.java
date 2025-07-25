package repository.custom.Impl;

import DataBase.DBConnection;
import model.dao.AppointmentDao;
import model.dao.PatientDao;
import model.dao.PrescriptionDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import repository.custom.PatientRepository;
import repository.custom.PrescriptionRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionRepositoryImpl implements PrescriptionRepository {


    //make the DB Connection
    Connection connection = DBConnection.getInstance().getConnection();

    public PrescriptionRepositoryImpl() throws SQLException {
    }


    @Override
    public boolean add(PrescriptionDao dao) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "INSERT INTO prescription(patient_id,doctor_id,medicine,dosage,duration)VALUES(?,?,?,?,?)";

        // Get patient ID by name(Cus Have to save the Patient Id relavant to patient name
        int patientId = getPatientIdByName(dao.getPatientName());
        if (patientId == -1) {
            throw new SQLException("Patient not found: " + dao.getPatientName());
        }

        // Get doctor ID by name
        int doctorId = getDoctorIdByName(dao.getDoctorName());
        if (doctorId == -1) {
            throw new SQLException("Doctor not found: " + dao.getDoctorName());
        }

        int rowAffected = runner.update(connection,sql,patientId,doctorId,dao.getMedicine(),dao.getDosage(),dao.getDuration());
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
    public PrescriptionDao searchByName(String patientName) throws SQLException {
        // First get the patient ID by name
        int patientId = getPatientIdByName(patientName);
        if (patientId == -1) {
            return null; // Patient not found
        }

        // Now search for prescription using patient_id and join with doctor table to get doctor name
        String sql = "SELECT p.prescription_id, pt.name as patient_name, d.name as doctor_name, " +
                "p.medicine, p.dosage, p.duration " +
                "FROM prescription p " +
                "JOIN patient pt ON p.patient_id = pt.patient_id " +
                "JOIN doctor d ON p.doctor_id = d.doctor_id " +
                "WHERE pt.name = ? " +
                "ORDER BY p.prescription_id DESC LIMIT 1"; // Get the most recent prescription

        QueryRunner runner = new QueryRunner();
        try {
            // Using custom ResultSetHandler to handle the result
            ResultSetHandler<PrescriptionDao> resultSetHandler = new ResultSetHandler<PrescriptionDao>() {
                @Override
                public PrescriptionDao handle(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        return new PrescriptionDao(
                                resultSet.getInt("prescription_id"),
                                resultSet.getString("patient_name"),
                                resultSet.getString("doctor_name"),
                                resultSet.getString("medicine"),
                                resultSet.getString("dosage"),
                                resultSet.getString("duration")
                        );
                    }
                    return null;
                }
            };

            // Execute query using DbUtils
            return runner.query(connection, sql, resultSetHandler, patientName);

        } catch (SQLException e) {
            throw new SQLException("Error searching prescription by patient name: " + patientName, e);
        }
    }

    @Override
    public List<PrescriptionDao> getAll() throws SQLException {

        QueryRunner runner = new QueryRunner();
        String sql = "SELECT a.prescription_id, p.name as patient_name, d.name as doctor_name, " +  // select appointment_id, name and the doctor name
                "a.medicine, a.dosage ,a.duration " + // select appointment dateand the time
                "FROM prescription a " + // from appointment table
                "JOIN patient p ON a.patient_id = p.patient_id " +  // connect patient_d with patient table(actual table which has the patient_id)
                "JOIN doctor d ON a.doctor_id = d.doctor_id " +  // connect the doctor_id with the doctor table which has the doctor_id
                "ORDER BY a.prescription_id";  // sort accordding to the appointment_id which means descending order


        ResultSetHandler<List<PrescriptionDao>> resultSetHandler = rs -> {
            List<PrescriptionDao> prescriptionDaoList = new ArrayList<>();

            while (rs.next()) {
                PrescriptionDao prescriptionDao = new PrescriptionDao();
                prescriptionDao.setPrescription_id(rs.getInt("prescription_id"));
                prescriptionDao.setPatientName(rs.getString("patient_name"));
                prescriptionDao.setDoctorName(rs.getString("doctor_name"));
                prescriptionDao.setMedicine(rs.getString("medicine"));
                prescriptionDao.setDosage(rs.getString("dosage"));
                prescriptionDao.setDuration(rs.getString("duration"));
                prescriptionDaoList.add(prescriptionDao);
            }
            return prescriptionDaoList;
        };

        return runner.query(connection,sql,resultSetHandler);

    }

    @Override
    public boolean update(PrescriptionDao prescriptionDao) throws SQLException {
        // Get patient ID by name
        int patientId = getPatientIdByName(prescriptionDao.getPatientName());
        if (patientId == -1) {
            throw new SQLException("Patient not found: " + prescriptionDao.getPatientName());
        }

        // Get doctor ID by name
        int doctorId = getDoctorIdByName(prescriptionDao.getDoctorName());
        if (doctorId == -1) {
            throw new SQLException("Doctor not found: " + prescriptionDao.getDoctorName());
        }

        QueryRunner runner = new QueryRunner();
        String sql = "UPDATE prescription SET patient_id = ?, doctor_id = ?, medicine = ?, dosage = ?, duration = ? WHERE prescription_id = ?";

        int rowsAffected = runner.update(connection, sql,
                patientId,
                doctorId,
                prescriptionDao.getMedicine(),
                prescriptionDao.getDosage(),
                prescriptionDao.getDuration(),
                prescriptionDao.getPrescription_id()
        );

        return rowsAffected > 0;
    }

    //--------------------------------------------------------------------------------------

    private int getPatientIdByName(String patientName) throws SQLException {

        QueryRunner runner = new QueryRunner();

        String sql = "SELECT patient_id FROM patient WHERE name = ?";

        Integer patient_id = runner.query(connection,sql, new ScalarHandler<>(),patientName);
        return patient_id != null ? patient_id : -1;


    }


    // ------------------------------------------------------------------------------------------------------


    private int getDoctorIdByName(String doctorName) throws SQLException {
        String sql = "SELECT doctor_id FROM doctor WHERE name = ?";


        QueryRunner runner = new QueryRunner();

        Integer doctor_id = runner.query(connection, sql, new ScalarHandler<>(), doctorName);

        return doctor_id != null ? doctor_id : -1;
    }
}
