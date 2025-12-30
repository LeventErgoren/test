package com.example.BirimTestleri;

import com.example.entity.Dues;
import com.example.entity.Flat;
import com.example.repository.DuesRepository;
import com.example.repository.FlatRepository;
import com.example.service.DuesService;
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

public class DuesServiceTest {

    @Mock
    DuesRepository duesRepository;

    @Mock
    FlatRepository flatRepository;

    @InjectMocks
    DuesService duesService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllDuesTest() {
        Dues dues = new Dues();
        when(duesRepository.findAll()).thenReturn(List.of(dues, dues));
        List<Dues> allDues = duesService.getAllDues();

        Assertions.assertEquals(2, allDues.size());
    }

    @Test
    public void assignDuesFlatNotFoundException() {
        Dues dues = new Dues();
        when(flatRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> duesService.assignDuesToFlat(1L, dues));
        Assertions.assertEquals("Daire bulunamadı", runtimeException.getMessage());
    }

    @Test
    public void assignDuesFlatIsEmptyException() {
        Dues dues = new Dues();
        Flat flat = new Flat();
        flat.setEmpty(true);
        when(flatRepository.findById(1L)).thenReturn(Optional.of(flat));

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> duesService.assignDuesToFlat(1L, dues));
        Assertions.assertEquals("Boş daireye aidat yansıtılamaz!", runtimeException.getMessage());
    }

    @Test
    public void assignDuesExistsException() {
        Dues dues = new Dues();
        dues.setMonth(1);
        dues.setYear(2025);
        Flat flat = new Flat();
        flat.setEmpty(false);
        when(flatRepository.findById(1L)).thenReturn(Optional.of(flat));
        when(duesRepository.existsByFlatIdAndMonthAndYear(1L, 1, 2025)).thenReturn(true);

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> duesService.assignDuesToFlat(1L, dues));
        Assertions.assertEquals(dues.getMonth() + ". ay için aidat zaten girilmiş!", runtimeException.getMessage());
    }

    @Test
    public void assignDuesToFlatTest() {
        Dues dues = new Dues();
        dues.setMonth(1);
        dues.setYear(2025);
        Flat flat = new Flat();
        flat.setEmpty(false);
        when(flatRepository.findById(1L)).thenReturn(Optional.of(flat));
        when(duesRepository.existsByFlatIdAndMonthAndYear(1L, 1, 2025)).thenReturn(false);
        dues.setFlat(flat);
        when(duesRepository.save(any(Dues.class))).thenReturn(dues);
        Dues dbDues = duesService.assignDuesToFlat(1L, dues);

        Assertions.assertEquals(dues.getMonth(), dbDues.getMonth());
        Assertions.assertEquals(flat.isEmpty(), dues.getFlat().isEmpty());
    }


}
