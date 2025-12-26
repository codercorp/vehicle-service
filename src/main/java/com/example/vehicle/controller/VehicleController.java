package com.example.vehicle.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.example.vehicle.repo.VehicleRepository;
import com.example.vehicle.model.Vehicle;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleRepository repo;

    @GetMapping("/vehicles")
    public List<Vehicle> all() {
        return repo.findAll();
    }

    @GetMapping("/vehicles/{id}")
    public Vehicle get(@PathVariable String id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping("/vehicle")
    public Vehicle create(@RequestBody Vehicle obj) {
        return repo.save(obj);
    }

    @PutMapping("/vehicle/{id}")
    public Vehicle update(@PathVariable String id, @RequestBody Vehicle obj) {
        obj.setId(id);
        return repo.save(obj);
    }

    @DeleteMapping("/vehicle/{id}")
    public void delete(@PathVariable String id) {
        repo.deleteById(id);
    }
}
