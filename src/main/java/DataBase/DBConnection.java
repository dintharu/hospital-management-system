package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


//Making the DBConnection Singleton
public class DBConnection {
    private static DBConnection instance;
    private final Connection connection;

    //Connecting the database to my sql datbase withh IntelliJ
    private DBConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
    }

    public static DBConnection getInstance() throws SQLException {
        return instance == null ? instance = new DBConnection() : instance;
    }

    public Connection getConnection() {
        return connection;
    }

}
