package model.dao;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DoctorDao {
    private Integer id;
    private String name;
    private String speciality;
    private String availability;
    private String qualifications;
    private String contact;
    private String availableTime;

    public DoctorDao(Integer id, String name, String speciality, String availability,
                     String qualifications, String contact) {
        this.id = id;
        this.name = name;
        this.speciality = speciality;
        this.availability = availability;
        this.qualifications = qualifications;
        this.contact = contact;
    }

}
