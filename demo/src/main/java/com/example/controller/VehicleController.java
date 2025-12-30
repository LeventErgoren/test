package com.example.controller;

import com.example.entity.Vehicle;
import com.example.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    // SENARYO: Max 2 araç kuralı burada tetiklenir
    @PostMapping("/resident/{residentId}")
    public ResponseEntity<Vehicle> addVehicleToResident(@PathVariable Long residentId, @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.addVehicleToResident(residentId, vehicle));
    }
}
