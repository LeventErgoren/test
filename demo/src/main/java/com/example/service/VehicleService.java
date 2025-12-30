package com.example.service;

import com.example.entity.Vehicle;
import com.example.repository.VehicleRepository;
import com.example.entity.Resident;
import com.example.repository.ResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ResidentRepository residentRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle addVehicleToResident(Long residentId, Vehicle vehicle) {
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new RuntimeException("Sakin bulunamadı"));

        // SENARYO 3 (KLASİK): Max 2 Araç
        int currentCount = vehicleRepository.countByResidentId(residentId);
        if (currentCount >= 2) {
            throw new RuntimeException("Sakin başına en fazla 2 araç tanımlanabilir!");
        }

        // Plaka Kontrolü
        if (vehicleRepository.existsByPlateNumber(vehicle.getPlateNumber())) {
             throw new RuntimeException("Bu plaka zaten sistemde kayıtlı!");
        }

        vehicle.setResident(resident);
        return vehicleRepository.save(vehicle);
    }
}
