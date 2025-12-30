package com.example.BirimTestleri;

import com.example.entity.Block;
import com.example.entity.Flat;
import com.example.repository.BlockRepository;
import com.example.repository.FlatRepository;
import com.example.service.FlatService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FlatServiceTest {

    @Mock
    FlatRepository flatRepository;

    @Mock
    BlockRepository blockRepository;

    @InjectMocks
    FlatService flatService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllFlatsTest() {
        Flat flat = new Flat();
        when(flatRepository.findAll()).thenReturn(List.of(flat, flat));
        List<Flat> allFlats = flatService.getAllFlats();

        Assertions.assertEquals(2, allFlats.size());
    }

    @Test
    public void getFlatByIdTest() {
        Flat flat = new Flat();
        flat.setId(1L);
        flat.setDoorNumber(10);
        when(flatRepository.findById(1L)).thenReturn(Optional.of(flat));
        Flat flatById = flatService.getFlatById(1L);

        Assertions.assertEquals(1L, flatById.getId());
        Assertions.assertEquals(10, flatById.getDoorNumber());
    }

    @Test
    public void getFlatByIdNotFoundException() {
        when(flatRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> flatService.getFlatById(1L));
    }

    @Test
    public void saveFlatBlockNotFoundException() {
        Flat flat = new Flat();
        Block block = new Block();
        block.setId(1L);
        flat.setBlock(block);
        when(blockRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> flatService.saveFlat(flat));
    }

    @Test
    public void saveFlat() {
        Flat flat = new Flat();
        Block block = new Block();
        block.setId(1L);
        block.setTotalFloors(5);
        flat.setBlock(block);
        flat.setId(1L);

        when(blockRepository.findById(1L)).thenReturn(Optional.of(block));
        when(flatRepository.countByBlockId(1L)).thenReturn(10);
        when(flatRepository.save(any(Flat.class))).thenReturn(flat);

        Flat dbFlat = flatService.saveFlat(flat);

        Assertions.assertEquals(1L, dbFlat.getId());
        Assertions.assertEquals(5, dbFlat.getBlock().getTotalFloors());
        Assertions.assertEquals(1L, dbFlat.getBlock().getId());
    }

    @Test
    public void saveFlatCapacityIsFullException() {
        Flat flat = new Flat();
        Block block = new Block();
        block.setId(1L);
        block.setTotalFloors(5);
        flat.setBlock(block);
        flat.setId(1L);

        when(blockRepository.findById(1L)).thenReturn(Optional.of(block));
        when(flatRepository.countByBlockId(1L)).thenReturn(25);

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> flatService.saveFlat(flat));
        Assertions.assertEquals("Bu blok kapasitesi dolmuş! Yeni daire eklenemez.", runtimeException.getMessage());

    }


}
