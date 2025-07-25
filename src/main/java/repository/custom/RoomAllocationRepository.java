package repository.custom;

import model.dao.PrescriptionDao;
import model.dao.RoomAllocationDao;
import repository.CRUDRepository;

import java.sql.SQLException;
import java.util.List;

public interface RoomAllocationRepository extends CRUDRepository<RoomAllocationDao,String> {
    boolean add(RoomAllocationDao dao) throws SQLException;

    boolean delete(String name) throws SQLException;

    RoomAllocationDao searchByName(String patientName) throws SQLException;

    List<RoomAllocationDao> getAll() throws SQLException;

    boolean update(RoomAllocationDao roomAllocationDao) throws SQLException;

}
