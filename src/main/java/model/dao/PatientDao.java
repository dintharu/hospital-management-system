package model.dao;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PatientDao {

    private Integer id;
    private String name;
    private int age;
    private String gender;
    private String contact;
    private String imergencyContact;
    private String medicalHistory;
}
