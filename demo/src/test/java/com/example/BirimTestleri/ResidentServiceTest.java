package com.example.BirimTestleri;

import com.example.entity.Flat;
import com.example.entity.Resident;
import com.example.repository.FlatRepository;
import com.example.repository.ResidentRepository;
import com.example.service.ResidentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ResidentServiceTest {

    @Mock
    ResidentRepository residentRepository;

    @Mock
    FlatRepository flatRepository;

    @InjectMocks
    ResidentService residentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllResidentsTest() {
        Resident resident = new Resident();
        when(residentRepository.findAll()).thenReturn(List.of(resident, resident));
        List<Resident> allResidents = residentService.getAllResidents();

        Assertions.assertEquals(2, allResidents.size());
    }

    @Test
    public void getResidentByIdError() {
        when(residentRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> residentService.getResidentById(1L));
    }

    @Test
    public void getResidentByIdTest() {
        Resident resident = new Resident();
        resident.setId(1L);
        when(residentRepository.findById(1L)).thenReturn(Optional.of(resident));
        Resident residentById = residentService.getResidentById(1L);

        Assertions.assertEquals(resident.getId(), residentById.getId());
    }

    @Test
    public void saveResidentExistsByEmailError() {
        Resident resident = new Resident();
        resident.setEmail("levent@gmail.com");
        when(residentRepository.existsByEmail("levent@gmail.com")).thenReturn(true);

        Assertions.assertThrows(RuntimeException.class, () -> residentService.saveResident(resident));
    }

    @Test
    public void saveResidentHasOwnerException() {
        Resident resident = new Resident();
        Flat flat = new Flat();
        flat.setId(1L);
        resident.setEmail("levent@gmail.com");
        resident.setOwner(true);
        resident.setFlat(flat);
        when(flatRepository.findById(anyLong())).thenReturn(Optional.of(flat));
        when(residentRepository.existsByEmail("levent@gmail.com")).thenReturn(false);
        when(residentRepository.existsByFlatIdAndIsOwnerTrue(1L)).thenReturn(true);

        Assertions.assertThrows(RuntimeException.class, () -> residentService.saveResident(resident));
    }

    @Test
    public void saveResidentTest() {
        Resident resident = new Resident();
        Flat flat = new Flat();
        flat.setId(1L);
        resident.setEmail("levent@gmail.com");
        resident.setOwner(true);
        resident.setFlat(flat);
        when(flatRepository.findById(anyLong())).thenReturn(Optional.of(flat));
        when(residentRepository.existsByEmail("levent@gmail.com")).thenReturn(false);
        when(residentRepository.existsByFlatIdAndIsOwnerTrue(1L)).thenReturn(false);
        when(residentRepository.save(any(Resident.class))).thenReturn(resident);

        Resident dbResident = residentService.saveResident(resident);
        Assertions.assertEquals(1L, dbResident.getFlat().getId());
    }

    @Test
    public void saveResidentSecondTest() {
        Resident resident = new Resident();
        Flat flat = new Flat();
        flat.setId(1L);
        resident.setEmail("levent@gmail.com");
        resident.setOwner(false);
        resident.setFlat(flat);
        when(flatRepository.findById(anyLong())).thenReturn(Optional.of(flat));
        when(residentRepository.existsByEmail("levent@gmail.com")).thenReturn(false);
        when(residentRepository.existsByFlatIdAndIsOwnerTrue(1L)).thenReturn(false);
        when(residentRepository.save(any(Resident.class))).thenReturn(resident);

        Resident dbResident = residentService.saveResident(resident);
        Assertions.assertEquals(1L, dbResident.getFlat().getId());
    }

    @Test
    public void saveResidentFlatNull_shouldSkipFirstIfAndSave() {
        Resident resident = new Resident();
        resident.setEmail("flatnull@gmail.com");
        resident.setOwner(false);
        resident.setFlat(null); // ilk if'e girmemeli

        when(residentRepository.existsByEmail("flatnull@gmail.com")).thenReturn(false);
        when(residentRepository.save(any(Resident.class))).thenReturn(resident);

        Resident dbResident = residentService.saveResident(resident);

        Assertions.assertEquals("flatnull@gmail.com", dbResident.getEmail());
        verifyNoInteractions(flatRepository);
    }

    @Test
    public void saveResidentFlatIdNotFound_shouldThrowDaireBulunamadi() {
        Resident resident = new Resident();
        Flat flat = new Flat();
        flat.setId(99L);
        resident.setEmail("notfound@gmail.com");
        resident.setOwner(false);
        resident.setFlat(flat);

        when(flatRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> residentService.saveResident(resident));
        Assertions.assertEquals("Daire bulunamadı", ex.getMessage());

        verify(residentRepository, never()).save(any(Resident.class));
    }

    @Test
    public void saveResidentFlatIsEmpty_shouldSetFalseAndSaveFlatThenResident() {
        Resident resident = new Resident();
        Flat flat = new Flat();
        flat.setId(1L);
        flat.setEmpty(true);
        resident.setEmail("emptyflat@gmail.com");
        resident.setOwner(false);
        resident.setFlat(flat);

        when(flatRepository.findById(1L)).thenReturn(Optional.of(flat));
        when(flatRepository.save(any(Flat.class))).thenAnswer(inv -> inv.getArgument(0));
        when(residentRepository.existsByEmail("emptyflat@gmail.com")).thenReturn(false);
        when(residentRepository.existsByFlatIdAndIsOwnerTrue(1L)).thenReturn(false);
        when(residentRepository.save(any(Resident.class))).thenReturn(resident);

        Resident dbResident = residentService.saveResident(resident);

        Assertions.assertFalse(flat.isEmpty());
        Assertions.assertEquals("emptyflat@gmail.com", dbResident.getEmail());
        verify(flatRepository, times(1)).save(any(Flat.class));
        verify(residentRepository, times(1)).save(any(Resident.class));
    }

    @Test
    public void saveResidentFlatIdNull_shouldSkipFirstIfAndContinueValidations() {
        Resident resident = new Resident();
        resident.setEmail("flatidnull@gmail.com");
        resident.setOwner(false);

        Flat flat = new Flat();
        flat.setId(null); // ilk if'e girmemeli: getId() == null
        resident.setFlat(flat);

        when(residentRepository.existsByEmail("flatidnull@gmail.com")).thenReturn(false);
        when(residentRepository.save(any(Resident.class))).thenReturn(resident);

        Resident dbResident = residentService.saveResident(resident);

        Assertions.assertEquals("flatidnull@gmail.com", dbResident.getEmail());
        verifyNoInteractions(flatRepository); // findById/save hiç çağrılmamalı
        verify(residentRepository, times(1)).save(any(Resident.class));
    }

}
