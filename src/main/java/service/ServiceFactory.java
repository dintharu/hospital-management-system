package service;

import service.custom.Impl.*;
import util.ServiceType;

import java.sql.SQLException;

public class ServiceFactory {

    private static ServiceFactory instance;

    private ServiceFactory(){

    }

    public static ServiceFactory getInstance() {
        return instance == null ? instance = new ServiceFactory() : instance;
    }

    public <T extends SuperService> T getServiceType(ServiceType type) throws SQLException {
        return switch (type) {
            case PATIENT -> (T) new PatientServiceImpl();
            case DOCTOR -> (T) new DoctorServiceImpl();
            case APPOINTMENT -> (T) new AppointmentServiceImpl();
            case EMAIL -> (T) new EmailServiceImpl();
            case PRESCRIPTION -> (T) new PrescriptionServiceImpl();
            case BILLING -> (T) new BillingServiceImpl();
            case ROOM -> (T) new RoomServiceImpl();
            case ROOMALLOCATION -> (T) new RoomAllocationServiceImpl();

            default -> throw new IllegalArgumentException("Unknown service type: " + type);
        };
    }
}
