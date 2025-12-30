package com.example.BirimTestleri;

import com.example.entity.Admin;
import com.example.repository.AdminRepository;
import com.example.service.AdminService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AdminServiceTest {

    @Mock
    AdminRepository adminRepository;

    @InjectMocks
    AdminService adminService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void loginNotFoundException() {
        when(adminRepository.findByUsernameAndPassword("Levent", "123")).thenReturn(Optional.empty());

        Admin admin = new Admin();
        admin.setUsername("Levent");
        admin.setPassword("123");

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> adminService.login(admin));
        Assertions.assertEquals("Kullanıcı adı veya şifre yanlış", runtimeException.getMessage());
    }

    @Test
    public void loginTest() {
        when(adminRepository.findByUsernameAndPassword(anyString(), anyString())).thenReturn(Optional.of(new Admin()));

        Admin admin = new Admin();
        admin.setUsername("Levent");
        admin.setPassword("123");
        boolean login = adminService.login(admin);

        Assertions.assertTrue(login);
    }

}
