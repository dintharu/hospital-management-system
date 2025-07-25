package service.custom;

import model.dao.RoomDao;
import model.dto.Room;
import service.SuperService;

import java.sql.SQLException;
import java.util.List;

public interface RoomService extends SuperService {

    boolean add(Room room) throws SQLException;

    boolean delete(Integer roomNumber) throws SQLException;

    RoomDao searchById(Integer roomNumber) throws SQLException;

    List<RoomDao> getAll() throws SQLException;

    boolean update(Room room, Integer originalRoomNumber) throws SQLException;


    List<Integer> getRoomNumbers() throws Exception;
}
