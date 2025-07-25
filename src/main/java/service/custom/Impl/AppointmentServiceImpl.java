package service.custom.Impl;

import model.dao.AppointmentDao;
import model.dao.DoctorDao;
import model.dto.Appointment;
import model.dto.Doctor;
import org.modelmapper.ModelMapper;
import repository.DAOFactory;
import repository.custom.AppointmentRepository;
import service.custom.AppointmentService;
import util.RepositoryType;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentServiceImpl implements AppointmentService {

    AppointmentRepository appointmentRepository = DAOFactory.getInstance().getServices(RepositoryType.APPOINTMENT);

    ModelMapper modelMapper = new ModelMapper();

    public AppointmentServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(Appointment appointment) throws SQLException {
        return appointmentRepository.add(modelMapper.map(appointment, AppointmentDao.class));

    }

    @Override
    public List<Appointment> getAll() throws SQLException {
        return appointmentRepository.getAll().stream().map(appointmentDao -> modelMapper.map(appointmentDao, Appointment.class)).collect(Collectors.toList());
    }

    @Override
    public boolean delete(String name) throws SQLException {
        return appointmentRepository.delete(name);
    }

    @Override
    public Appointment searchByName(String patientName) throws SQLException {
        return modelMapper.map(appointmentRepository.searchByName(patientName), Appointment.class);
    }

    @Override
    public boolean update(Appointment appointment) throws SQLException {
        return appointmentRepository.update(modelMapper.map(appointment, AppointmentDao.class));
    }
}
