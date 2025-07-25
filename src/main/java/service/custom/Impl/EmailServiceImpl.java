package service.custom.Impl;

import model.dto.Appointment;
import model.dto.Patient;
import service.custom.EmailService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailServiceImpl implements EmailService {


    private final String fromEmail = "laknathoki@gmail.com";
    private final String password = "mvst dtte mxze srgh";


    @Override
    public boolean sendAppointmentConfirmation(Patient patient, Appointment appointment) {
        String subject = "Appointment Done -  Medical Centre";
        String content  = createEmailContent(patient,appointment);
        return sendEmail(patient,subject,content);
    }


    private boolean sendEmail(Patient patient,String subject, String content){
        try {
            // Set system properties to enable TLS protocols
            System.setProperty("jdk.tls.client.protocols", "TLSv1.2,TLSv1.3");
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
            System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
            System.setProperty("mail.smtp.ssl.trust", "*");

            // Disable SSL certificate validation (for testing only - not recommended for production)
            System.setProperty("mail.smtp.ssl.checkserveridentity", "false");

            // Gmail SMTP configuration with SSL compatibility
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");

            // SSL/TLS configuration
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.ssl.checkserveridentity", "false");

            // Additional compatibility settings
            props.put("mail.smtp.socketFactory.fallback", "true");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.connectiontimeout", "10000");

            // Create session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(patient.getContact()));
            message.setSubject(subject);
            message.setText(content);

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully to: " + patient.getContact());
            return true;

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();

            // Try fallback method with port 465 (SSL)
            return sendEmailFallback(patient, subject,content);
        }
    }

    @Override
    public boolean sendAppointmentUpdate(Patient patient, Appointment appointment) {
        String subject = "Appointment updated -  Medical Centre";
        String content  = updateEmailContent(patient,appointment);
        return sendEmail(patient,subject,content);
    }

    @Override
    public boolean sendAppointmentCancel(Patient patient, Appointment appointment) {
        String subject = "Appointment Done -  Medical Centre";
        String content  = CancelEmailContent(patient,appointment);
        return sendEmail(patient,subject,content);
    }

    private boolean sendEmailFallback(Patient patient, String subject, String content) {
        try {
            System.out.println("Attempting fallback email method with SSL port 465...");

            // Set system properties for SSL
            System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
            System.setProperty("mail.smtp.ssl.trust", "*");

            // SSL configuration using port 465
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.ssl.checkserveridentity", "false");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");

            // Create session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(patient.getContact()));
            message.setSubject(subject);
            message.setText(content);

            // Send email
            Transport.send(message);
            System.out.println("Fallback email sent successfully to: " + patient.getContact());
            return true;

        } catch (Exception e) {
            System.err.println("Fallback email method also failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String createEmailContent(Patient patient, Appointment appointment) {
        StringBuilder content = new StringBuilder();
        content.append("APPOINTMENT CONFIRMATION\n");
        content.append("========================\n\n");
        content.append("Dear ").append(patient.getName()).append(",\n\n");
        content.append("Your appointment has been successfully booked. Here are the details:\n\n");
        content.append("Patient Name: ").append(appointment.getPatientName()).append("\n");
        content.append("Doctor: ").append(appointment.getDoctorName()).append("\n");
        content.append("Date: ").append(appointment.getDate()).append("\n");
        content.append("Time: ").append(appointment.getTime()).append("\n\n");
        content.append("IMPORTANT NOTES:\n");
        content.append("- Please arrive 15 minutes before your appointment time\n");
        content.append("- Bring your ID and any relevant medical documents\n");
        content.append("- If you need to reschedule, please contact us at least 24 hours in advance\n\n");
        content.append("Thank you for choosing our medical center.\n\n");
        content.append("Best regards,\n");
        content.append("Medical Center Team\n\n");
        content.append("---\n");
        content.append("This is an automated message. Please do not reply to this email.");

        return content.toString();
    }


    private String updateEmailContent(Patient patient, Appointment appointment) {
        StringBuilder content = new StringBuilder();
        content.append("APPOINTMENT CONFIRMATION\n");
        content.append("========================\n\n");
        content.append("Dear ").append(patient.getName()).append(",\n\n");
        content.append("Your appointment has been successfully updated. Here are the new details:\n\n");
        content.append("Patient Name: ").append(appointment.getPatientName()).append("\n");
        content.append("Doctor: ").append(appointment.getDoctorName()).append("\n");
        content.append("Date: ").append(appointment.getDate()).append("\n");
        content.append("Time: ").append(appointment.getTime()).append("\n\n");
        content.append("IMPORTANT NOTES:\n");
        content.append("- Please arrive 15 minutes before your appointment time\n");
        content.append("- Bring your ID and any relevant medical documents\n");
        content.append("- If you need to reschedule, please contact us at least 24 hours in advance\n\n");
        content.append("Thank you for choosing our medical center.\n\n");
        content.append("Best regards,\n");
        content.append("Medical Center Team\n\n");
        content.append("---\n");
        content.append("This is an automated message. Please do not reply to this email.");

        return content.toString();
    }

    private String CancelEmailContent(Patient patient, Appointment appointment) {
        StringBuilder content = new StringBuilder();
        content.append("APPOINTMENT CONFIRMATION\n");
        content.append("========================\n\n");
        content.append("Dear ").append(patient.getName()).append(",\n\n");
        content.append("Your appointment has been successfully Canceled.");
        content.append("Thank you for choosing our medical center.\n\n");
        content.append("Best regards,\n");
        content.append("Medical Center Team\n\n");
        content.append("---\n");
        content.append("This is an automated message. Please do not reply to this email.");

        return content.toString();
    }
}
