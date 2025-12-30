package com.example.controller;

import com.example.entity.Resident;
import com.example.service.ResidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/residents")
@RequiredArgsConstructor
public class ResidentController {

    private final ResidentService residentService;

    @GetMapping
    public ResponseEntity<List<Resident>> getAllResidents() {
        return ResponseEntity.ok(residentService.getAllResidents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resident> getResidentById(@PathVariable Long id) {
        return ResponseEntity.ok(residentService.getResidentById(id));
    }

    // SENARYO: Tek ev sahibi kuralı burada tetiklenir
    @PostMapping
    public ResponseEntity<Resident> createResident(@RequestBody Resident resident) {
        return ResponseEntity.ok(residentService.saveResident(resident));
    }
}
