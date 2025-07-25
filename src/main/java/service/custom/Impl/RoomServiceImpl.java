package service.custom.Impl;

import model.dao.PatientDao;
import model.dao.PrescriptionDao;
import model.dao.RoomDao;
import model.dto.Doctor;
import model.dto.Room;
import org.modelmapper.ModelMapper;
import repository.DAOFactory;
import repository.custom.RoomRepository;
import service.custom.RoomService;
import util.RepositoryType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomServiceImpl implements RoomService {


    RoomRepository roomRepository = DAOFactory.getInstance().getServices(RepositoryType.ROOM);

    ModelMapper modelMapper = new ModelMapper();

    public RoomServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(Room room) throws SQLException {
        return roomRepository.add(modelMapper.map(room, RoomDao.class));
    }

    @Override
    public boolean delete(Integer roomNumber) throws SQLException {
        return roomRepository.delete(roomNumber);
    }

    @Override
    public RoomDao searchById(Integer roomNumber) throws SQLException {
        return modelMapper.map(roomRepository.searchById(roomNumber), RoomDao.class);
    }

    @Override
    public List<RoomDao> getAll() throws SQLException {
        return  roomRepository.getAll()
                .stream()
                .map(roomEntity -> modelMapper.map(roomEntity, RoomDao.class))
                .toList();
    }

    @Override
    public boolean update(Room room , Integer originalRoomNumber) throws SQLException {
        RoomDao roomDao = modelMapper.map(room, RoomDao.class);
        return roomRepository.update(roomDao, originalRoomNumber);

    }

    @Override
    public List<Integer> getRoomNumbers() throws Exception {
        List<RoomDao> all = getAll();
        ArrayList<Integer> RoomNumberlist = new ArrayList<>();
        all.forEach(roomNumbers->{
            RoomNumberlist.add(roomNumbers.getRoomNumber());
        });
        return RoomNumberlist;
    }
}
