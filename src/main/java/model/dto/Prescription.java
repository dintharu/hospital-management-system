package model.dto;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Prescription {
    private Integer prescription_id;
    private String patientName;
    private String doctorName;
    private String medicine;
    private String dosage;
    private String duration;
}
