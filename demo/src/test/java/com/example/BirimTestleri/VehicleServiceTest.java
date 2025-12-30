package com.example.BirimTestleri;

import com.example.entity.Resident;
import com.example.entity.Vehicle;
import com.example.repository.ResidentRepository;
import com.example.repository.VehicleRepository;
import com.example.service.VehicleService;
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

public class VehicleServiceTest {

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    ResidentRepository residentRepository;

    @InjectMocks
    VehicleService vehicleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllVehiclesTest() {
        Vehicle vehicle = new Vehicle();
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle, vehicle));
        List<Vehicle> allVehicles = vehicleService.getAllVehicles();
        Assertions.assertEquals(2, allVehicles.size());
    }

    @Test
    public void addVehicleToResidentNotFountException() {
        Vehicle vehicle = new Vehicle();
        when(residentRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> vehicleService.addVehicleToResident(1L, vehicle));
        Assertions.assertEquals("Sakin bulunamadı", runtimeException.getMessage());
    }

    @Test
    public void addVehicleCurrentCountException() {
        Vehicle vehicle = new Vehicle();
        when(residentRepository.findById(1L)).thenReturn(Optional.of(new Resident()));
        when(vehicleRepository.countByResidentId(1L)).thenReturn(2);
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> vehicleService.addVehicleToResident(1L, vehicle));
        Assertions.assertEquals("Sakin başına en fazla 2 araç tanımlanabilir!", runtimeException.getMessage());
    }

    @Test
    public void addVehiclePlateAldreadyExistsException() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber("65ABC885");
        when(residentRepository.findById(1L)).thenReturn(Optional.of(new Resident()));
        when(vehicleRepository.countByResidentId(1L)).thenReturn(1);
        when(vehicleRepository.existsByPlateNumber("65ABC885")).thenReturn(true);

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> vehicleService.addVehicleToResident(1L, vehicle));
        Assertions.assertEquals("Bu plaka zaten sistemde kayıtlı!", runtimeException.getMessage());
    }

    @Test
    public void addVehicleTest() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber("65ABC885");
        Resident resident = new Resident();
        resident.setFirstName("Levent");
        resident.setPhoneNumber("1234");

        when(residentRepository.findById(1L)).thenReturn(Optional.of(resident));
        when(vehicleRepository.countByResidentId(1L)).thenReturn(1);
        when(vehicleRepository.existsByPlateNumber("65ABC885")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        Vehicle dbVehicle = vehicleService.addVehicleToResident(1L, vehicle);
        Assertions.assertEquals("65ABC885", dbVehicle.getPlateNumber());
        Assertions.assertEquals("Levent", vehicle.getResident().getFirstName());
        Assertions.assertEquals("1234", vehicle.getResident().getPhoneNumber());
    }



}
