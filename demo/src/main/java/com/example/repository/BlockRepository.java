package com.example.repository;

import com.example.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    // Validasyon: Aynı isimde blok oluşturulamaz
    boolean existsByName(String name);
}
