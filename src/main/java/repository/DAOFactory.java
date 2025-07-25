package repository;

import repository.custom.AppointmentRepository;
import repository.custom.Impl.*;
import service.custom.Impl.DoctorServiceImpl;
import util.RepositoryType;

import java.sql.SQLException;

public class DAOFactory {

private static DAOFactory instance;

// Make repository instances (Makes objects)
private DAOFactory(){

}

public static DAOFactory getInstance(){
return instance== null ?new DAOFactory() : instance;
}

    public <T extends SuperRepository> T getServices(RepositoryType type) throws SQLException {


        return switch (type){
            case PATIENT ->(T) new PatientRepositoryImpl();
            case DOCTOR -> (T) new DoctorRepositoryImpl();
            case APPOINTMENT -> (T) new AppointmentRepositoryImpl();
            case PRESCRIPTION -> (T) new PrescriptionRepositoryImpl();
            case BILLING -> (T) new BillingRepositoryImpl();
            case ROOM -> (T) new RoomRepositoryImpl();
            case ROOMALLOCATION -> (T) new RoomAllocationRepositoryImpl();
            default -> throw new IllegalArgumentException("Unknown service type: " + type);
        };
    }

}
