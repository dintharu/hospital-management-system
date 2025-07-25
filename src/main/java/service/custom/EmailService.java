package service.custom;

import model.dto.Appointment;
import model.dto.Patient;
import service.SuperService;

public interface EmailService extends SuperService {
    boolean sendAppointmentConfirmation(Patient patient, Appointment appointment);
    boolean sendAppointmentUpdate(Patient patient, Appointment appointment);
    boolean sendAppointmentCancel(Patient patient,Appointment appointment);
}
