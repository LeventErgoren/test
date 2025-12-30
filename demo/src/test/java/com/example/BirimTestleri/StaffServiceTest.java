package com.example.BirimTestleri;

import com.example.entity.Staff;
import com.example.repository.StaffRepository;
import com.example.service.StaffService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.when;

public class StaffServiceTest {

    @Mock
    StaffRepository staffRepository;

    @InjectMocks
    StaffService staffService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllStafTest() {
        Staff staff = new Staff();
        when(staffRepository.findAll()).thenReturn(List.of(staff, staff));
        List<Staff> allStaff = staffService.getAllStaff();

        Assertions.assertEquals(2, allStaff.size());
    }

    @Test
    public void saveStaffTest() {
        Staff staff = new Staff();
        Staff dbStaff = new Staff();
        dbStaff.setId(1L);
        when(staffRepository.save(staff)).thenReturn(dbStaff);
        Staff serviceStaff = staffService.saveStaff(staff);

        Assertions.assertEquals(dbStaff.getId(), serviceStaff.getId());
    }

}
