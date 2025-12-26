package com.example.vehicle.controller;

import com.example.vehicle.model.Vehicle;
import com.example.vehicle.repo.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleRepository repo;

    @Autowired
    private ObjectMapper objectMapper;

    private Vehicle mockVehicle() {
        Vehicle v = new Vehicle();
        v.setId("1");
        v.setModel("Nexon");
        v.setMake("Tata");
        v.setType("hatchback");
        return v;
    }

    // ---------------- GET ALL ----------------
    @Test
    void all_success() throws Exception {

        Mockito.when(repo.findAll())
                .thenReturn(List.of(mockVehicle()));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].model").value("Nexon"))
                .andExpect(jsonPath("$[0].make").value("Tata"));
    }

    // ---------------- GET BY ID ----------------
    @Test
    void get_success() throws Exception {

        Mockito.when(repo.findById("1"))
                .thenReturn(Optional.of(mockVehicle()));

        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.model").value("Nexon"))
                .andExpect(jsonPath("$.make").value("Tata"));
    }

    // ---------------- CREATE ----------------
    @Test
    void create_success() throws Exception {

        Mockito.when(repo.save(Mockito.any(Vehicle.class)))
                .thenReturn(mockVehicle());

        mockMvc.perform(post("/api/vehicle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockVehicle())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.model").value("Nexon"));
    }

    // ---------------- UPDATE ----------------
    @Test
    void update_success() throws Exception {

        Vehicle updated = mockVehicle();
        updated.setModel("Harrier");

        Mockito.when(repo.save(Mockito.any(Vehicle.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/vehicle/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Harrier"));
    }

    // ---------------- DELETE ----------------
    @Test
    void delete_success() throws Exception {

        Mockito.doNothing().when(repo).deleteById("1");

        mockMvc.perform(delete("/api/vehicle/1"))
                .andExpect(status().isOk());

        Mockito.verify(repo, Mockito.times(1)).deleteById("1");
    }
}
