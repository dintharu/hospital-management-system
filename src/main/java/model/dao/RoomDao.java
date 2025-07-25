package model.dao;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class RoomDao {


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
