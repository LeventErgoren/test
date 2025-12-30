package com.example.controller;

import com.example.entity.Block;
import com.example.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @GetMapping
    public ResponseEntity<List<Block>> getAllBlocks() {
        return ResponseEntity.ok(blockService.getAllBlocks());
    }

    @PostMapping
    public ResponseEntity<Block> createBlock(@RequestBody Block block) {
        return ResponseEntity.ok(blockService.saveBlock(block));
    }

    // SENARYO: Dolu blok silinemez testi buradan tetiklenir
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlock(@PathVariable Long id) {
        blockService.deleteBlock(id);
        return ResponseEntity.ok().build();
    }
}
