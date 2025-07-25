package service.custom.Impl;

import model.dao.PrescriptionDao;
import model.dto.Appointment;
import model.dto.Prescription;
import org.modelmapper.ModelMapper;
import repository.DAOFactory;
import repository.custom.PrescriptionRepository;
import service.custom.PrescriptionService;
import util.RepositoryType;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class PrescriptionServiceImpl implements PrescriptionService {


    PrescriptionRepository prescriptionRepository = DAOFactory.getInstance().getServices(RepositoryType.PRESCRIPTION);

    ModelMapper modelMapper = new ModelMapper();

    public PrescriptionServiceImpl() throws SQLException {
    }


    @Override
    public boolean add(Prescription prescription) throws SQLException {
        return prescriptionRepository.add(modelMapper.map(prescription, PrescriptionDao.class));
    }

    @Override
    public List<Prescription> getAll() throws SQLException {
        return prescriptionRepository.getAll().stream().map(prescriptionDao -> modelMapper.map(prescriptionDao, Prescription.class)).collect(Collectors.toList());
    }

    @Override
    public boolean delete(String name) throws SQLException {
        return prescriptionRepository.delete(name);
    }

    @Override
    public Prescription searchByName(String patientName) throws SQLException {
        return modelMapper.map(prescriptionRepository.searchByName(patientName),Prescription.class);
    }

    @Override
    public boolean update(Prescription prescription) throws SQLException {
        return prescriptionRepository.update(modelMapper.map(prescription,PrescriptionDao.class));
    }
}
