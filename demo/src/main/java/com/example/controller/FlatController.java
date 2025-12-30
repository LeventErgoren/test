package com.example.controller;

import com.example.entity.Flat;
import com.example.service.FlatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/flats")
@RequiredArgsConstructor
public class FlatController {

    private final FlatService flatService;

    @GetMapping
    public ResponseEntity<List<Flat>> getAllFlats() {
        return ResponseEntity.ok(flatService.getAllFlats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flat> getFlatById(@PathVariable Long id) {
        return ResponseEntity.ok(flatService.getFlatById(id));
    }

    // SENARYO: Blok kapasite kontrolü burada tetiklenir
    @PostMapping
    public ResponseEntity<Flat> createFlat(@RequestBody Flat flat) {
        return ResponseEntity.ok(flatService.saveFlat(flat));
    }
}
