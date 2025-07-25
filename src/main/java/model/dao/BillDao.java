package model.dao;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BillDao {

    private Integer billId;
    private String patientName;
    private Integer totalAmount;
    private String paymentStatus;
    private LocalDate date;
}
