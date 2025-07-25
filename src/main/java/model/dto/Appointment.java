package model.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Appointment {

    private Integer id;
    private String patientName;
    private String doctorName;
    private String date;
    private String time;
}
