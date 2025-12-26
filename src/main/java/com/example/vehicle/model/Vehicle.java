package com.example.vehicle.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "vehicles")
@Data
@NoArgsConstructor
public class Vehicle {
    @Id
    private String id;
    private String registrationNumber;
    private String make;
    private String model;
    private String type;
    private String ownerId;

}
