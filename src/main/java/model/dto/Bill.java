package model.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Bill {

    private Integer billId;
    private String patientName;
    private Integer totalAmount;
    private String paymentStatus;
    private LocalDate date;

}
