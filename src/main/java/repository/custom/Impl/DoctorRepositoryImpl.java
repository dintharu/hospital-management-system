package repository.custom.Impl;

import DataBase.DBConnection;
import model.dao.DoctorDao;
import model.dao.PatientDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import repository.custom.DoctorRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorRepositoryImpl implements DoctorRepository {

    //Make the  DB Connection
    Connection connection = DBConnection.getInstance().getConnection();

    public DoctorRepositoryImpl() throws SQLException {
    }

    @Override
    public boolean add(DoctorDao dao) throws SQLException {
        String sql = "INSERT INTO doctor(name,specialty,availability,qualifications,contact_details)VALUES(?,?,?,?,?)";

        QueryRunner runner = new QueryRunner();
        int rowAffected = runner.update(connection,sql,dao.getName(),dao.getSpeciality(),dao.getAvailability() + " , " + dao.getAvailableTime(),dao.getQualifications(),dao.getContact());
        return rowAffected>0;
    }


    // ------------------------------------------------------------------------------------------------------

    @Override
    public boolean delete(String name) throws SQLException {
        String sql = "DELETE FROM doctor WHERE name = ?";

        QueryRunner runner = new QueryRunner();
        int rowAffected = runner.update(connection,sql,name);
        return rowAffected>0;
    }


    // ------------------------------------------------------------------------------------------------------


    @Override
    public DoctorDao searchByName(String name) throws SQLException {
        String sql = "SELECT * FROM doctor WHERE name = ?";

        QueryRunner runner = new QueryRunner();
        try {
            ResultSetHandler<DoctorDao> resultSetHandler = new ResultSetHandler<DoctorDao>() {
                @Override
                public DoctorDao handle(ResultSet resultSet) throws SQLException {
                    if(resultSet.next()){
                        String availability = resultSet.getString("availability");
                        String day = "";
                        String time = "";

                        // Split the availability to separate day and time
                        if(availability != null && availability.contains(" , ")){
                            String[] parts = availability.split(" , ");
                            if(parts.length >= 1){
                                day = parts[0];
                            }
                            if(parts.length >= 2){
                                time = parts[1];
                            }
                        } else {
                            day = availability != null ? availability : "";
                        }

                        // Create DoctorDao with separated day and time
                        DoctorDao doctorDao = new DoctorDao(
                                resultSet.getInt("doctor_id"),
                                resultSet.getString("name"),
                                resultSet.getString("specialty"),
                                day, // Just the day
                                resultSet.getString("qualifications"),
                                resultSet.getString("contact_details")
                        );

                        // Set the time separately if your DoctorDao has this method
                        doctorDao.setAvailableTime(time);

                        return doctorDao;
                }
                    return null;
            }

            };
            return runner.query(connection,sql,resultSetHandler,name);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ------------------------------------------------------------------------------------------------------


    @Override
    public List<DoctorDao> getAll() throws SQLException {

        String sql = "SELECT * FROM doctor";

        QueryRunner  runner = new QueryRunner();

        try {
            PreparedStatement preparedStatementGetAll = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatementGetAll.executeQuery();

            ResultSetHandler<List<DoctorDao>> resultSetHandler = new ResultSetHandler<List<DoctorDao>>() {
                @Override
                public List<DoctorDao> handle(ResultSet resultSet) throws SQLException {
                    List<DoctorDao>doctorDaos = new ArrayList<>();

                    while (resultSet.next()){
                        DoctorDao  doctorDao = new DoctorDao(
                                resultSet.getInt("doctor_id"),
                                resultSet.getString("name"),
                                resultSet.getString("specialty"),
                                resultSet.getString("availability"),
                                resultSet.getString("qualifications"),
                                resultSet.getString("contact_details")
                        );
                        doctorDaos.add(doctorDao);
                    }
                    return doctorDaos;
                }
            };
            return runner.query(connection,sql,resultSetHandler);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ------------------------------------------------------------------------------------------------------


    @Override
    public boolean update(DoctorDao dao, String originalName) throws SQLException {
        String sql = "UPDATE doctor SET name=?, specialty=?, availability=?, qualifications=?, contact_details=? WHERE name=?";

        QueryRunner runner = new QueryRunner();
        int rowAffected = runner.update(connection,sql,dao.getName(),dao.getSpeciality(),(String) dao.getAvailability() + " , " + dao.getAvailableTime(),dao.getQualifications(),dao.getContact(),originalName);
        return rowAffected>0;
    }
}
