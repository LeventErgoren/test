package com.example.repository;

import com.example.entity.Flat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FlatRepository extends JpaRepository<Flat, Long> {
    // Bir bloktaki daire sayısı (Kapasite kontrolü senaryosu için)
    int countByBlockId(Long blockId);

    List<Flat> findByBlockId(Long blockId);
}
