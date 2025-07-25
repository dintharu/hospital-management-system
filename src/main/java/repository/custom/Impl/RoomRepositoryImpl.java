package repository.custom.Impl;

import DataBase.DBConnection;
import model.dao.RoomDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import repository.custom.RoomRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomRepositoryImpl implements RoomRepository {

    Connection connection = DBConnection.getInstance().getConnection();

    public RoomRepositoryImpl() throws SQLException {
    }

    @Override
    public boolean add(RoomDao roomDao) throws SQLException {
        String sql = "INSERT INTO Room(room_number,room_type,capacity,floor_number,daily_rate,status,amenities,created_date)VALUES(?,?,?,?,?,?,?,?)";

        QueryRunner runner = new QueryRunner();
        int rowAffected = runner.update(connection,sql,
                roomDao.getRoomNumber(),
                roomDao.getRoomType(),
                roomDao.getCapacity(),
                roomDao.getFloorNumber(),
                roomDao.getDailyRate(),
                roomDao.getStatus(),
                roomDao.getAmenities(),
                roomDao.getDate());
        return rowAffected>0;
    }


    @Override
    public boolean delete(Integer roomNumber) throws SQLException {
        String sql = "DELETE FROM room WHERE room_number = ?";

        QueryRunner runner = new QueryRunner();
        int rowAffected = runner.update(connection,sql,roomNumber);
        return rowAffected>0;
    }

    @Override
    public RoomDao searchById(Integer roomNumber) throws SQLException {
        String sql = "SELECT * FROM room WHERE room_number = ?";

        QueryRunner runner = new QueryRunner();
        try {
            // Using custom ResultSetHandler to handle the result
            ResultSetHandler<RoomDao> resultSetHandler = new ResultSetHandler<RoomDao>() {
                @Override
                public RoomDao handle(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        return new RoomDao(
                                resultSet.getInt("room_id"),
                                resultSet.getInt("room_number"),
                                resultSet.getString("room_type"),
                                resultSet.getInt("capacity"),
                                resultSet.getInt("floor_number"),
                                resultSet.getDouble("daily_rate"),
                                resultSet.getString("status"),
                                resultSet.getString("amenities"),
                                resultSet.getDate("created_date").toLocalDate()
                        );
                    }
                    return null;
                }
            };

            // Execute query using DbUtils
            return runner.query(connection, sql, resultSetHandler, roomNumber);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RoomDao> getAll() throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "SELECT * FROM room";

        try {

            ResultSetHandler<List<RoomDao>>resultSetHandler = new ResultSetHandler<List<RoomDao>>() {
                @Override
                public List<RoomDao> handle(ResultSet resultSet) throws SQLException {
                    List<RoomDao>roomDaos = new ArrayList<>();

                    while (resultSet.next()){
                        RoomDao roomDao = new RoomDao(
                                resultSet.getInt("room_id"),
                                resultSet.getInt("room_number"),
                                resultSet.getString("room_type"),
                                resultSet.getInt("capacity"),
                                resultSet.getInt("floor_number"),
                                resultSet.getDouble("daily_rate"),
                                resultSet.getString("status"),
                                resultSet.getString("amenities"),
                                resultSet.getDate("created_date").toLocalDate());
                        roomDaos.add(roomDao);
                    }
                    return roomDaos;
                }
            };
            return runner.query(connection,sql,resultSetHandler);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(RoomDao roomDao , Integer originalRoomNumber) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "UPDATE room SET room_number = ?, room_type = ?, capacity = ?, floor_number = ?, daily_rate = ?, status = ?, amenities = ? WHERE room_number = ?";

        int rowsAffected = runner.update(connection, sql,
                roomDao.getRoomNumber(),
                roomDao.getRoomType(),
                roomDao.getCapacity(),
                roomDao.getFloorNumber(),
                roomDao.getDailyRate(),
                roomDao.getStatus(),
                roomDao.getAmenities(),
                originalRoomNumber
        );

        return rowsAffected > 0;
    }

    @Override
    public boolean delete(String name) throws SQLException {
        throw new UnsupportedOperationException("Delete by name is not supported for Room. Use delete(Integer roomId) instead.");
    }

    @Override
    public RoomDao searchByName(String name) throws SQLException {
        throw new UnsupportedOperationException("Search by name is not supported for Room. Use searchById(Integer roomId) instead.");
    }

}
