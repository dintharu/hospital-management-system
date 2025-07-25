package service.custom.Impl;

import model.dao.PatientDao;
import model.dto.Patient;
import org.modelmapper.ModelMapper;
import repository.DAOFactory;
import repository.custom.PatientRepository;
import service.custom.PatientService;
import util.RepositoryType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PatientServiceImpl implements PatientService {

    PatientRepository patientRepository = DAOFactory.getInstance().getServices(RepositoryType.PATIENT);

    ModelMapper  modelMapper = new ModelMapper();

    public PatientServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(Patient patient) throws SQLException {
        return patientRepository.add(modelMapper.map(patient, PatientDao.class));
    }

    @Override
    public List<Patient> getAll() throws SQLException {
        return patientRepository.getAll().stream().map(patientDao -> modelMapper.map(patientDao, Patient.class)).collect(Collectors.toList());
    }

    @Override
    public boolean delete(String name) throws SQLException {
        return patientRepository.delete(name);
    }

    @Override
    public Patient searchByName(String name) throws SQLException {
        return modelMapper.map(patientRepository.searchByName(name), Patient.class);
    }

    @Override
    public boolean update(Patient  patient, String originalName) throws SQLException {
        PatientDao patientDao = modelMapper.map(patient, PatientDao.class);
        return patientRepository.update(patientDao, originalName);
    }

    @Override
    public List<String> getPatientNames() throws SQLException {
        List<Patient> all = getAll();
        ArrayList<String> patientList = new ArrayList<>();
        all.forEach(patient->{
            patientList.add(patient.getName());
        });
        return patientList;
    }
}
