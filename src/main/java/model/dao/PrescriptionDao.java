package model.dao;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PrescriptionDao {
    private Integer prescription_id;
    private String patientName;
    private String doctorName;
    private String medicine;
    private String dosage;
    private String duration;

}
