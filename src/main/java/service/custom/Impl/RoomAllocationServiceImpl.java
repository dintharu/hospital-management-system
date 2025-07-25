package service.custom.Impl;

import model.dao.PrescriptionDao;
import model.dao.RoomAllocationDao;
import model.dto.Prescription;
import model.dto.RoomAllocation;
import org.apache.poi.ss.formula.functions.Mode;
import org.modelmapper.ModelMapper;
import repository.DAOFactory;
import repository.custom.RoomAllocationRepository;
import service.custom.RoomAllocationService;
import util.RepositoryType;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class RoomAllocationServiceImpl implements RoomAllocationService {

    RoomAllocationRepository roomAllocationRepository = DAOFactory.getInstance().getServices(RepositoryType.ROOMALLOCATION);

    ModelMapper modelMapper = new ModelMapper();

    public RoomAllocationServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(RoomAllocation roomAllocation) throws SQLException {
        return roomAllocationRepository.add(modelMapper.map(roomAllocation, RoomAllocationDao.class));
    }

    @Override
    public List<RoomAllocation> getAll() throws SQLException {
        return roomAllocationRepository.getAll().stream().map(roomAllocationDao -> modelMapper.map(roomAllocationDao, RoomAllocation.class)).collect(Collectors.toList());
    }

    @Override
    public boolean delete(String name) throws SQLException {
        return roomAllocationRepository.delete(name);
    }

    @Override
    public RoomAllocation searchByName(String patientName) throws SQLException {
        return modelMapper.map(roomAllocationRepository.searchByName(patientName),RoomAllocation.class);
    }

    @Override
    public boolean update(RoomAllocation roomAllocation) throws SQLException {
        return roomAllocationRepository.update(modelMapper.map(roomAllocation,RoomAllocationDao.class));
    }
}
