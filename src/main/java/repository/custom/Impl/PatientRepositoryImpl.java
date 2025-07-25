package repository.custom.Impl;

import DataBase.DBConnection;
import model.dao.PatientDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import repository.custom.PatientRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientRepositoryImpl implements PatientRepository {

    //DB Connection obbject
    Connection connection = DBConnection.getInstance().getConnection();

    public PatientRepositoryImpl() throws SQLException {
    }

    // ------------------------------------------------------------------------------------------------------


    // ADD Patient to the database
    @Override
    public boolean add(PatientDao dao) throws SQLException {

        String sql = "INSERT INTO patient(name,age,gender,contact_details,emergency_contact,medical_history)VALUES(?,?,?,?,?,?)";

        QueryRunner runner = new QueryRunner();
        int rowAffected = runner.update(connection,sql,dao.getName(),dao.getAge(),dao.getGender(),dao.getContact(),dao.getImergencyContact(),dao.getMedicalHistory());
        return rowAffected>0;
    }


    // ------------------------------------------------------------------------------------------------------


    //Delete Patient from databse
    @Override
    public boolean delete(String name) throws SQLException {
        String sql = "DELETE FROM patient WHERE name = ?";

        QueryRunner runner = new QueryRunner();
        int rowAffected = runner.update(connection,sql,name);
        return rowAffected>0;
    }


    // ------------------------------------------------------------------------------------------------------


    //Search the Patient from the name
    @Override
    public PatientDao searchByName(String name) throws SQLException {
        String sql = "SELECT * FROM patient WHERE name = ?";

        QueryRunner runner = new QueryRunner();
        try {
            // Using custom ResultSetHandler to handle the result
            ResultSetHandler<PatientDao> resultSetHandler = new ResultSetHandler<PatientDao>() {
                @Override
                public PatientDao handle(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        return new PatientDao(
                                resultSet.getInt("patient_id"),
                                resultSet.getString("name"),
                                resultSet.getInt("age"),
                                resultSet.getString("gender"),
                                resultSet.getString("contact_details"),
                                resultSet.getString("emergency_contact"),
                                resultSet.getString("medical_history")
                        );
                    }
                    return null;
                }
            };

            // Execute query using DbUtils
            return runner.query(connection, sql, resultSetHandler, name);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ------------------------------------------------------------------------------------------------------


    //Get All Patients at once
    @Override
    public List<PatientDao> getAll() throws SQLException {


        QueryRunner runner = new QueryRunner();
        String sql = "SELECT * FROM patient";

        try {

            ResultSetHandler<List<PatientDao>>resultSetHandler = new ResultSetHandler<List<PatientDao>>() {
                @Override
                public List<PatientDao> handle(ResultSet resultSet) throws SQLException {
                    List<PatientDao>patients = new ArrayList<>();

                    while (resultSet.next()){
                        PatientDao  patientDao = new PatientDao(resultSet.getInt("patient_id"),resultSet.getString("name"),resultSet.getInt("age"),resultSet.getString("gender"),resultSet.getString("contact_details"),resultSet.getString("emergency_contact"),resultSet.getString("medical_history"));
                        patients.add(patientDao);
                    }
                    return patients;
                }
            };
            return runner.query(connection,sql,resultSetHandler);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ------------------------------------------------------------------------------------------------------


    @Override
    public boolean update(PatientDao dao, String originalName) throws SQLException {
        String sql = "UPDATE patient SET name=?, age=?, gender=?, contact_details=?, emergency_contact=?, medical_history=? WHERE name=?";

        QueryRunner runner = new QueryRunner();
        int rowAffected = runner.update(connection,sql,dao.getName(),dao.getAge(),(String) dao.getGender(),dao.getContact(),dao.getImergencyContact(),dao.getMedicalHistory(),originalName);
        return rowAffected>0;

    }
}
