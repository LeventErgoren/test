package com.example.BirimTestleri;

import com.example.entity.FlatType;
import com.example.repository.FlatTypeRepository;
import com.example.service.FlatTypeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.when;

public class FlatTypeServiceTest {

    @Mock
    FlatTypeRepository flatTypeRepository;

    @InjectMocks
    FlatTypeService flatTypeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllFlatTypesTest() {
        FlatType flatType = new FlatType();
        when(flatTypeRepository.findAll()).thenReturn(List.of(flatType, flatType));
        List<FlatType> allFlatTypes = flatTypeService.getAllFlatTypes();

        Assertions.assertEquals(2, allFlatTypes.size());
    }

    @Test
    public void saveFlatTypeTest() {
        FlatType flatType = new FlatType();
        FlatType savedType = new FlatType();
        savedType.setId(1L);
        when(flatTypeRepository.save(flatType)).thenReturn(savedType);
        FlatType dbType = flatTypeService.saveFlatType(flatType);

        Assertions.assertEquals(1L, dbType.getId());
    }


}
