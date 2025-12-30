package com.example.EntegrasyonTestleri;

import com.example.controller.VehicleController;
import com.example.entity.Vehicle;
import com.example.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
public class VehicleControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    VehicleService vehicleService;

    @Test
    public void getAllVehiclesTest() throws Exception {
        Vehicle vehicle = new Vehicle();
        when(vehicleService.getAllVehicles()).thenReturn(List.of(vehicle, vehicle));

        mockMvc.perform(get("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void addVehicleToResidentTest() throws Exception {
        Long residentId = 1L;
        Vehicle vehicle = new Vehicle();
        vehicle.setId(10L);

        when(vehicleService.addVehicleToResident(any(Long.class), any(Vehicle.class))).thenReturn(vehicle);

        mockMvc.perform(post("/api/vehicles/resident/{residentId}", residentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }
}
