package com.example.service;

import com.example.entity.Flat;
import com.example.entity.Block;
import com.example.repository.FlatRepository;
import com.example.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlatService {

    private final FlatRepository flatRepository;
    private final BlockRepository blockRepository;

    public List<Flat> getAllFlats() {
        return flatRepository.findAll();
    }

    public Flat saveFlat(Flat flat) {
        Block block = blockRepository.findById(flat.getBlock().getId())
                .orElseThrow(() -> new RuntimeException("Blok bulunamadı"));

        // SENARYO 1 (ZOR): Kapasite Kontrolü
        // Varsayalım her katta 4 daire var. Toplam kapasite = katsayısı * 4
        int maxCapacity = block.getTotalFloors() * 4;
        int currentFlatCount = flatRepository.countByBlockId(block.getId());

        if (currentFlatCount >= maxCapacity) {
            throw new RuntimeException("Bu blok kapasitesi dolmuş! Yeni daire eklenemez.");
        }

        return flatRepository.save(flat);
    }
    
    public Flat getFlatById(Long id) {
        return flatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Daire bulunamadı"));
    }
}
