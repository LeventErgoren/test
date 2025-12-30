package com.example.controller;

import com.example.entity.Dues;
import com.example.service.DuesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dues")
@RequiredArgsConstructor
public class DuesController {

    private final DuesService duesService;

    @GetMapping
    public ResponseEntity<List<Dues>> getAllDues() {
        return ResponseEntity.ok(duesService.getAllDues());
    }

    // SENARYO: Mükerrer aidat ve Boş daire kontrolü burada tetiklenir
    @PostMapping("/flat/{flatId}")
    public ResponseEntity<Dues> assignDuesToFlat(@PathVariable Long flatId, @RequestBody Dues dues) {
        return ResponseEntity.ok(duesService.assignDuesToFlat(flatId, dues));
    }
}
