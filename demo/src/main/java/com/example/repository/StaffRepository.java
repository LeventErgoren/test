package com.example.repository;

import com.example.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    // Belirli bir maaşın üzerindeki personeller (Raporlama testi için)
    List<Staff> findBySalaryGreaterThan(java.math.BigDecimal amount);
}
