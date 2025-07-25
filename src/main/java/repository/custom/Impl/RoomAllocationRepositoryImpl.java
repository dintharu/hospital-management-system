package repository.custom.Impl;

import DataBase.DBConnection;
import model.dao.PrescriptionDao;
import model.dao.RoomAllocationDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import repository.custom.RoomAllocationRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomAllocationRepositoryImpl implements RoomAllocationRepository {

    Connection connection = DBConnection.getInstance().getConnection();

    public RoomAllocationRepositoryImpl() throws SQLException {
    }

    @Override
    public boolean add(RoomAllocationDao dao) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "INSERT INTO roomallocation(patient_id,room_id,admission_date,discharge_date,status,notes,allocated_by,total_days,total_cost)VALUES(?,?,?,?,?,?,?,?,?)";

        // Get patient ID by name(Cus Have to save the Patient Id relavant to patient name
        int patientId = getPatientIdByName(dao.getPatinetName());
        if (patientId == -1) {
            throw new SQLException("Patient not found: " + dao.getPatinetName());
        }

        // Get doctor ID by name
        int doctorId = getDoctorIdByName(dao.getAllocatedBy());
        if (doctorId == -1) {
            throw new SQLException("Doctor not found: " + dao.getAllocatedBy());
        }

        //Get the room ID by room number
        int roomId = getRoomIdByRoomNumber(dao.getRoomNumber()); // Assuming roomNumber is the room ID in the DAO
        if (roomId == -1) {
            throw new SQLException("Invalid room number: " + dao.getRoomNumber());
        }

        int rowAffected = runner.update(connection,sql,patientId,roomId,dao.getAdmissionDate(),dao.getDischargeDate(),dao.getStatus(),dao.getNotes(),doctorId,dao.getTotalDays(),dao.getTotalCost());
        return rowAffected>0;
    }



    @Override
    public boolean delete(String name) throws SQLException {
        QueryRunner runner = new QueryRunner();
        //  Delete appointment by patient name using JOIN
        String sql = "DELETE a FROM roomallocation a " +  //delete the records from appointment table
                "JOIN patient p ON a.patient_id = p.patient_id " +  //connect appointment table with the patient table from the foreighn key patient_id
                "WHERE p.name = ?"; // now its search the relevant name which we entered and find the relevant patient_id for that

        int rowAffected = runner.update(connection,sql,name);
        return rowAffected > 0;    }

    @Override
    public RoomAllocationDao searchByName(String patientName) throws SQLException {
        // First get the patient ID by name
        int patientId = getPatientIdByName(patientName);
        if (patientId == -1) {
            return null; // Patient not found
        }


        // Now search for prescription using patient_id and join with doctor table to get doctor name
        String sql = "SELECT p.allocation_id, pt.name as patient_name, d.name as doctor_name, " +
                "r.room_number, p.admission_date, p.discharge_date, p.status, p.notes, " +
                "p.total_days, p.total_cost " +
                "FROM roomallocation p " +
                "JOIN patient pt ON p.patient_id = pt.patient_id " +
                "JOIN doctor d ON p.allocated_by = d.doctor_id " +
                "JOIN room r ON p.room_id = r.room_id " +
                "WHERE pt.name = ?";

        QueryRunner runner = new QueryRunner();
        try {
            // Using custom ResultSetHandler to handle the result
            ResultSetHandler<RoomAllocationDao> resultSetHandler = new ResultSetHandler<RoomAllocationDao>() {
                @Override
                public RoomAllocationDao handle(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        RoomAllocationDao roomAllocationDao = new RoomAllocationDao(
                                resultSet.getInt("allocation_id"),
                                resultSet.getString("patient_name"),
                                resultSet.getInt("room_number"),
                                resultSet.getDate("admission_date").toLocalDate(),
                                resultSet.getDate("discharge_date").toLocalDate(),
                                resultSet.getString("status"),
                                resultSet.getString("notes"),
                                resultSet.getString("doctor_name"),
                                resultSet.getInt("total_days"),
                                resultSet.getDouble("total_cost")
                        );
                        return roomAllocationDao;
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
    public List<RoomAllocationDao> getAll() throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "SELECT a.allocation_id, p.name as patient_name, d.name as doctor_name, " +
                "a.room_id, a.admission_date, a.discharge_date, a.status, a.notes, " +
                "a.total_days, a.total_cost " +
                "FROM roomallocation a " +
                "JOIN patient p ON a.patient_id = p.patient_id " +
                "JOIN doctor d ON a.allocated_by = d.doctor_id";


        ResultSetHandler<List<RoomAllocationDao>> resultSetHandler = rs -> {
            List<RoomAllocationDao>roomAllocationDaos = new ArrayList<>();

            while (rs.next()) {
                RoomAllocationDao roomAllocationDao = new RoomAllocationDao();
                roomAllocationDao.setAllocationId(rs.getInt("allocation_id"));
                roomAllocationDao.setPatinetName(rs.getString("patient_name"));
                roomAllocationDao.setAllocatedBy(rs.getString("doctor_name"));
                roomAllocationDao.setRoomNumber(rs.getInt("room_id"));
                roomAllocationDao.setAdmissionDate(rs.getDate("admission_date").toLocalDate());
                roomAllocationDao.setDischargeDate(rs.getDate("discharge_date").toLocalDate());
                roomAllocationDao.setStatus(rs.getString("status"));
                roomAllocationDao.setNotes(rs.getString("notes"));
                roomAllocationDao.setTotalDays(rs.getInt("total_days"));
                roomAllocationDao.setTotalCost(rs.getDouble("total_cost"));
                roomAllocationDaos.add(roomAllocationDao);
            }
            return roomAllocationDaos;
        };

        return runner.query(connection,sql,resultSetHandler);
    }

    @Override
    public boolean update(RoomAllocationDao roomAllocationDao) throws SQLException {
        // Get patient ID by name
        int patientId = getPatientIdByName(roomAllocationDao.getPatinetName());
        if (patientId == -1) {
            throw new SQLException("Patient not found: " + roomAllocationDao.getPatinetName());
        }

        // Get doctor ID by name
        int doctorId = getDoctorIdByName(roomAllocationDao.getAllocatedBy());
        if (doctorId == -1) {
            throw new SQLException("Doctor not found: " + roomAllocationDao.getAllocationId());
        }

        //Get the room ID by room number
        int roomId = getRoomIdByRoomNumber(roomAllocationDao.getRoomNumber()); // Assuming roomNumber is the room ID in the DAO
        if (roomId == -1) {
            throw new SQLException("Invalid room number: " + roomAllocationDao.getRoomNumber());
        }


        QueryRunner runner = new QueryRunner();
        String sql = "UPDATE roomallocation SET patient_id = ?, room_id = ?, admission_date = ?, discharge_date = ?, status = ?, notes = ? , allocated_by = ? ,total_days = ? , total_cost = ? WHERE allocation_id = ?";

        int rowsAffected = runner.update(connection, sql,
                patientId,
                roomId,
                roomAllocationDao.getAdmissionDate(),
                roomAllocationDao.getDischargeDate(),
                roomAllocationDao.getStatus(),
                roomAllocationDao.getNotes(),
                doctorId,
                roomAllocationDao.getTotalDays(),
                roomAllocationDao.getTotalCost(),
                roomAllocationDao.getAllocationId()
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

    private int getRoomIdByRoomNumber(int roomId) {

        String sql = "SELECT room_id FROM room WHERE room_number = ?";

        QueryRunner runner = new QueryRunner();

        try {
            Integer id = runner.query(connection, sql, new ScalarHandler<>(), roomId);
            return id != null ? id : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return -1 if an error occurs
        }
    }


}
