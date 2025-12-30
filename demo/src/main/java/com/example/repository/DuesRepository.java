package com.example.repository;

import com.example.entity.Dues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DuesRepository extends JpaRepository<Dues, Long> {
    
    // SENARYO: Mükerrer aidat engelleme
    boolean existsByFlatIdAndMonthAndYear(Long flatId, int month, int year);

    java.util.Optional<Dues> findByFlatIdAndMonthAndYear(Long flatId, int month, int year);

    // Bir daireye ait tüm borçlar
    List<Dues> findByFlatId(Long flatId);
}
