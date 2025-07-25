package service.custom.Impl;

import model.dao.DoctorDao;
import model.dao.PatientDao;
import model.dto.Doctor;
import model.dto.Patient;
import org.modelmapper.ModelMapper;
import repository.DAOFactory;
import repository.custom.DoctorRepository;
import service.custom.DoctorService;
import util.RepositoryType;

import javax.print.Doc;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorServiceImpl implements DoctorService {

    DoctorRepository doctorRepository = DAOFactory.getInstance().getServices(RepositoryType.DOCTOR);

    ModelMapper modelMapper = new ModelMapper();

    public DoctorServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(Doctor doctor) throws SQLException {
        return doctorRepository.add(modelMapper.map(doctor, DoctorDao.class));
    }

    @Override
    public List<Doctor> getAll() throws SQLException {
        return doctorRepository.getAll().stream().map(doctorDao -> modelMapper.map(doctorDao, Doctor.class)).collect(Collectors.toList());
    }

    @Override
    public boolean delete(String name) throws SQLException {
        return doctorRepository.delete(name);
    }

    @Override
    public Doctor searchByName(String name) throws SQLException {
        return modelMapper.map(doctorRepository.searchByName(name), Doctor.class);
    }

    @Override
    public boolean update(Doctor doctor, String originalName) throws SQLException {
        DoctorDao doctorDao = modelMapper.map(doctor, DoctorDao.class);
        return doctorRepository.update(doctorDao, originalName);
    }

    @Override
    public List<String> getDoctorNames() throws SQLException {
        List<Doctor> all = getAll();
        ArrayList<String> doctorList = new ArrayList<>();
        all.forEach(doctor->{
            doctorList.add(doctor.getName());
        });
        return doctorList;
    }
}
