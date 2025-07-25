package repository.custom;

import model.dao.RoomDao;
import repository.CRUDRepository;

import java.sql.SQLException;
import java.util.List;

public interface RoomRepository extends CRUDRepository<RoomDao,Integer> {

    boolean add(RoomDao  roomDao) throws SQLException;

    boolean delete(Integer roomNumber) throws SQLException;

    RoomDao searchById(Integer roomNumber) throws SQLException;

    List<RoomDao> getAll() throws SQLException;

    boolean update(RoomDao roomDao , Integer originalRoomNumber) throws SQLException;


}
