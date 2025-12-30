package com.example.controller;

import com.example.entity.Admin;
import com.example.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.login(admin));
    }
}
