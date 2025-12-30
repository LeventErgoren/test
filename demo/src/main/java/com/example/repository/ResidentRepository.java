package com.example.repository;

import com.example.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {
    
    // SENARYO: Bir dairede zaten ev sahibi var mı? (Tek ev sahibi kuralı)
    boolean existsByFlatIdAndIsOwnerTrue(Long flatId);

    // İletişim bilgisi kontrolü
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    List<Resident> findByFlatId(Long flatId);
}
