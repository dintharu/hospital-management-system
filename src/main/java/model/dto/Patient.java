package model.dto;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Patient {

    private int id;
    private String name;
    private int age;
    private String gender;
    private String contact;
    private String imergencyContact;
    private String medicalHistory;
}
