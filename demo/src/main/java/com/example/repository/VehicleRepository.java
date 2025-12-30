package com.example.repository;

import com.example.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // SENARYO: Bir sakinin kaç aracı var? (Max 2 araç kuralı)
    int countByResidentId(Long residentId);

    boolean existsByPlateNumber(String plateNumber);
    
    List<Vehicle> findByResidentId(Long residentId);
}
