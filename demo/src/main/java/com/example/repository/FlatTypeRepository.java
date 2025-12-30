package com.example.repository;

import com.example.entity.FlatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlatTypeRepository extends JpaRepository<FlatType, Long> {
    boolean existsByTypeName(String typeName);
}
