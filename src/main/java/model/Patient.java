package model;


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
    private int contact;
    private int imergencyContact;
    private String medicalHistory;
}
