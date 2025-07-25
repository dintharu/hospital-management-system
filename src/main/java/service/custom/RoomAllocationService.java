package service.custom;

import model.dto.Prescription;
import model.dto.RoomAllocation;
import service.SuperService;

import java.sql.SQLException;
import java.util.List;

public interface RoomAllocationService  extends SuperService {

    boolean add(RoomAllocation roomAllocation) throws SQLException;

    List<RoomAllocation> getAll() throws SQLException;

    boolean delete(String name) throws SQLException;

    RoomAllocation searchByName(String patientName) throws SQLException;

    boolean update(RoomAllocation roomAllocation) throws SQLException;

}
