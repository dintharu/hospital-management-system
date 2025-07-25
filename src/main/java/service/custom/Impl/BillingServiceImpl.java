package service.custom.Impl;

import model.dao.BillDao;
import model.dao.PatientDao;
import model.dto.Bill;
import org.modelmapper.ModelMapper;
import repository.DAOFactory;
import repository.custom.BillingRepository;
import service.custom.BillingService;
import util.RepositoryType;

import java.sql.SQLException;
import java.util.List;

public class BillingServiceImpl implements BillingService {

    BillingRepository billingRepository = DAOFactory.getInstance().getServices(RepositoryType.BILLING);

    ModelMapper modelMapper = new ModelMapper();

    public BillingServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(Bill bill) throws SQLException {
        return billingRepository.add(modelMapper.map(bill, BillDao.class));
    }

    @Override
    public List<Bill> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public boolean delete(String name) throws SQLException {
        return false;
    }

    @Override
    public Bill searchByName(String name) throws SQLException {
        return null;
    }

    @Override
    public boolean update(Bill bill) throws SQLException {
        return false;
    }
}
