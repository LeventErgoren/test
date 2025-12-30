package com.example.service;

import com.example.entity.Block;
import com.example.repository.BlockRepository;
import com.example.repository.FlatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final FlatRepository flatRepository;

    public List<Block> getAllBlocks() {
        return blockRepository.findAll();
    }

    public Block saveBlock(Block block) {
        // VALIDATION TESTİ: Aynı isimde blok olamaz
        if (blockRepository.existsByName(block.getName())) {
            throw new RuntimeException("Bu isimde bir blok zaten mevcut!");
        }
        return blockRepository.save(block);
    }

    public void deleteBlock(Long id) {
        // LOGIC TESTİ: Dolu blok silinemez
        if (!flatRepository.findByBlockId(id).isEmpty()) {
            throw new RuntimeException("İçinde daire bulunan bir blok silinemez! Önce daireleri taşıyın.");
        }
        blockRepository.deleteById(id);
    }
}
