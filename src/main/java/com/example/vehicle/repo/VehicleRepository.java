package com.example.vehicle.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.vehicle.model.Vehicle;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {}
