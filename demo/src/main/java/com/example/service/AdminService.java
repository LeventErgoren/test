package com.example.service;

import com.example.entity.Admin;
import com.example.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public boolean login(Admin admin) {
        Admin dbAdmin = adminRepository.findByUsernameAndPassword(admin.getUsername(), admin.getPassword()).orElseThrow(() -> new RuntimeException("Kullanıcı adı veya şifre yanlış"));
        return true;
    }

}
