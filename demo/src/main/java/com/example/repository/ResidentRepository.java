package com.example.repository;

import com.example.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {

    boolean existsByFlatIdAndIsOwnerTrue(Long flatId);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    List<Resident> findByFlatId(Long flatId);
}
