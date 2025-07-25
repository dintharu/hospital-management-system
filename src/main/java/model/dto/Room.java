package model.dto;

import lombok.*;
import service.custom.DoctorService;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Room {


    private  Integer roomId;
    private  Integer roomNumber;
    private  String roomType;
    private  Integer capacity;
    private  Integer floorNumber;
    private Double dailyRate;
    private  String status;
    private  String  amenities;
    private LocalDate date;

}
