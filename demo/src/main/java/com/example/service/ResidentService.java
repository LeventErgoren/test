package com.example.service;

import com.example.entity.Resident;
import com.example.entity.Flat;
import com.example.repository.FlatRepository;
import com.example.repository.ResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResidentService {

    private final ResidentRepository residentRepository;
    private final FlatRepository flatRepository;

    public List<Resident> getAllResidents() {
        return residentRepository.findAll();
    }

    public Resident saveResident(Resident resident) {
        if (resident.getFlat() != null && resident.getFlat().getId() != null) {
            Flat flat = flatRepository.findById(resident.getFlat().getId())
                    .orElseThrow(() -> new RuntimeException("Daire bulunamadı"));
            if (flat.isEmpty()) {
                flat.setEmpty(false);
                flatRepository.save(flat);
            }
        }

        // Validasyon
        if (residentRepository.existsByEmail(resident.getEmail())) {
            throw new RuntimeException("Bu email zaten kullanımda.");
        }

        // SENARYO 2 (ZOR): Tek Ev Sahibi Kuralı
        // Eğer eklenen kişi "Ev Sahibi" ise, o dairede başka ev sahibi var mı bak.
        if (resident.isOwner()) {
            boolean hasOwner = residentRepository.existsByFlatIdAndIsOwnerTrue(resident.getFlat().getId());
            if (hasOwner) {
                throw new RuntimeException("Bir dairenin sadece 1 tane yasal sahibi (Owner) olabilir!");
            }
        }

        return residentRepository.save(resident);
    }
    
    public Resident getResidentById(Long id) {
         return residentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sakin bulunamadı"));
    }
}
