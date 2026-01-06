package com.example.repository;

import com.example.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    int countByResidentId(Long residentId);

    boolean existsByPlateNumber(String plateNumber);
    
    List<Vehicle> findByResidentId(Long residentId);
}
