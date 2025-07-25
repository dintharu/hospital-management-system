package model.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomAllocation {

    private Integer allocationId;
    private String patinetName;
    private Integer roomNumber;
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private String status;
    private String notes;
    private String allocatedBy;
    private int totalDays;
    private Double totalCost;
}
