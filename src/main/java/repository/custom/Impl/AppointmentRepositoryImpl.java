package repository.custom.Impl;

import DataBase.DBConnection;
import model.dao.AppointmentDao;
import model.dao.DoctorDao;
import model.dao.PatientDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import repository.custom.AppointmentRepository;

import javax.management.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class AppointmentRepositoryImpl implements AppointmentRepository {


    Connection connection = DBConnection.getInstance().getConnection();

    public AppointmentRepositoryImpl() throws SQLException {
    }


    // ------------------------------------------------------------------------------------------------------


    @Override
    public boolean add(AppointmentDao dao) throws SQLException {

        QueryRunner runner = new QueryRunner();
        String sql = "INSERT INTO appointment(patient_id,doctor_id,appointment_date,time)VALUES(?,?,?,?)";

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

            int rowAffected = runner.update(connection,sql,patientId,doctorId,String.valueOf(dao.getDate()),dao.getTime());
            return rowAffected>0;

    }


    // ------------------------------------------------------------------------------------------------------


    @Override
    public boolean delete(String patientName) throws SQLException {

        QueryRunner runner = new QueryRunner();
        //  Delete appointment by patient name using JOIN
        String sql = "DELETE a FROM appointment a " +  //delete the records from appointment table
                "JOIN patient p ON a.patient_id = p.patient_id " +  //connect appointment table with the patient table from the foreighn key patient_id
                "WHERE p.name = ?"; // now its search the relevant name which we entered and find the relevant patient_id for that

            int rowAffected = runner.update(connection,sql,patientName);
            return rowAffected > 0;

    }


    // ------------------------------------------------------------------------------------------------------


    @Override
    public AppointmentDao searchByName(String patientName) throws SQLException {

        QueryRunner runner = new QueryRunner();

        String sql = "SELECT a.appointment_id, p.name as patient_name, d.name as doctor_name, " +  // select appointment_id , patient name and doctor name
                "a.appointment_date, a.time " + //select appintment_week available day and the time relevant for the doctor
                "FROM appointment a " + // from appointmetnt table
                "JOIN patient p ON a.patient_id = p.patient_id " + // Join the tables
                "JOIN doctor d ON a.doctor_id = d.doctor_id " + // Join the tables
                "WHERE p.name = ? " +  //check for the selected patient name
                "ORDER BY a.appointment_id DESC " + // Sort the appointment_id to  descending order
                "LIMIT 1"; // Get the most recent appointment(returns the first result)

        ResultSetHandler <AppointmentDao>resultSetHandler = rs -> {
            if (rs.next()) {
                AppointmentDao appointmentDao = new AppointmentDao();
                appointmentDao.setId(rs.getInt("appointment_id"));
                appointmentDao.setPatientName(rs.getString("patient_name"));
                appointmentDao.setDoctorName(rs.getString("doctor_name"));
                appointmentDao.setDate(rs.getString("appointment_date"));
                appointmentDao.setTime(rs.getString("time"));
                return appointmentDao;
            }
            return null; // If nothing found return nothing
        };
        return runner.query (connection,sql,resultSetHandler,patientName);
    }


    // ------------------------------------------------------------------------------------------------------


    @Override
    public List<AppointmentDao> getAll() throws SQLException {

        QueryRunner runner = new QueryRunner();
        String sql = "SELECT a.appointment_id, p.name as patient_name, d.name as doctor_name, " +  // select appointment_id, name and the doctor name
                "a.appointment_date, a.time " + // select appointment dateand the time
                "FROM appointment a " + // from appointment table
                "JOIN patient p ON a.patient_id = p.patient_id " +  // connect patient_d with patient table(actual table which has the patient_id)
                "JOIN doctor d ON a.doctor_id = d.doctor_id " +  // connect the doctor_id with the doctor table which has the doctor_id
                "ORDER BY a.appointment_id";  // sort accordding to the appointment_id which means descending order


        ResultSetHandler<List<AppointmentDao>> resultSetHandler = rs -> {
            List<AppointmentDao> appointmentList = new ArrayList<>();

            while (rs.next()) {
                AppointmentDao appointmentDao = new AppointmentDao();
                appointmentDao.setId(rs.getInt("appointment_id"));
                appointmentDao.setPatientName(rs.getString("patient_name"));
                appointmentDao.setDoctorName(rs.getString("doctor_name"));
                appointmentDao.setDate(rs.getString("appointment_date"));
                appointmentDao.setTime(rs.getString("time"));
                appointmentList.add(appointmentDao);
            }
            return appointmentList;
        };

        return runner.query(connection,sql,resultSetHandler);
    }


    // ------------------------------------------------------------------------------------------------------


    @Override
    public boolean update(AppointmentDao appointmentDao) throws SQLException {
        // Update appointment based on patient name
        
        QueryRunner runner = new QueryRunner();

        String sql = "UPDATE appointment a " +
                "JOIN patient p ON a.patient_id = p.patient_id " +
                "SET a.doctor_id = ?, a.appointment_date = ?, a.time = ? " +
                "WHERE p.name = ?";
        
            // Get doctor ID by name
            int doctorId = getDoctorIdByName(appointmentDao.getDoctorName());
            if (doctorId == -1) {
                throw new SQLException("Doctor not found: " + appointmentDao.getDoctorName());
            }

            int rowsAffected = runner.update(connection,sql,doctorId,appointmentDao.getDate(),appointmentDao.getTime(),appointmentDao.getPatientName());
            return rowsAffected > 0;

        
    }


    // ------------------------------------------------------------------------------------------------------


    private int getPatientIdByName(String patientName) throws SQLException {

        QueryRunner runner = new QueryRunner();

        String sql = "SELECT patient_id FROM patient WHERE name = ?";

        Integer patient_id = runner.query(connection,sql, new ScalarHandler<>(),patientName);
        return patient_id != null ? patient_id : -1;
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, patientName);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                return rs.getInt("patient_id");
//            }
//        }
//        return -1;
    }


    // ------------------------------------------------------------------------------------------------------


    private int getDoctorIdByName(String doctorName) throws SQLException {
        String sql = "SELECT doctor_id FROM doctor WHERE name = ?";


        QueryRunner runner = new QueryRunner();

        Integer doctor_id = runner.query(connection, sql, new ScalarHandler<>(), doctorName);

        return doctor_id != null ? doctor_id : -1;
    }
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, doctorName);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                return rs.getInt("doctor_id");
//            }
//        }
//        return -1; // Not found
//    }
}
