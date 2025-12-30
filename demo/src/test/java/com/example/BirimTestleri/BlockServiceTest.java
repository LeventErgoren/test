package com.example.BirimTestleri;

import com.example.entity.Block;
import com.example.entity.Flat;
import com.example.repository.BlockRepository;
import com.example.repository.FlatRepository;
import com.example.service.BlockService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class BlockServiceTest {

    @Mock
    BlockRepository blockRepository;

    @Mock
    FlatRepository flatRepository;

    @InjectMocks
    BlockService blockService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllBlocksTest() {
        Block block = new Block();
        when(blockRepository.findAll()).thenReturn(List.of(block, block));
        List<Block> allBlocks = blockService.getAllBlocks();
        Assertions.assertEquals(2, allBlocks.size());
    }

    @Test
    public void saveBlockTest() {
        Block block = new Block(null, "Cevizkent", 4);
        Block savedBlock = new Block(1L, "Cevizkent", 4);
        when(blockRepository.existsByName(block.getName())).thenReturn(false);
        when(blockRepository.save(block)).thenReturn(savedBlock);

        Block dbBlock = blockService.saveBlock(block);

        Assertions.assertEquals(dbBlock.getId(), savedBlock.getId());
        Assertions.assertEquals(dbBlock.getName(), savedBlock.getName());
    }

    @Test
    public void saveBlockExistsByNameException() {
        Block block = new Block(null, "Cevizkent", 4);
        when(blockRepository.existsByName(block.getName())).thenReturn(true);
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> blockService.saveBlock(block));
        Assertions.assertEquals("Bu isimde bir blok zaten mevcut!", runtimeException.getMessage());
    }

    @Test
    public void deleteBlockTest() {
        when(flatRepository.findByBlockId(1L)).thenReturn(List.of());
        doNothing().when(blockRepository).deleteById(1L);

        blockService.deleteBlock(1L);
    }

    @Test
    public void deleteBlockException(){
        Flat flat = new Flat();
        when(flatRepository.findByBlockId(1L)).thenReturn(List.of(flat));
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> blockService.deleteBlock(1L));
        Assertions.assertEquals("İçinde daire bulunan bir blok silinemez! Önce daireleri taşıyın.", runtimeException.getMessage());
    }


}
