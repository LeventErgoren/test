package com.example.controller;

import com.example.entity.FlatType;
import com.example.service.FlatTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/flat-types")
@RequiredArgsConstructor
public class FlatTypeController {

    private final FlatTypeService flatTypeService;

    @GetMapping
    public ResponseEntity<List<FlatType>> getAllFlatTypes() {
        return ResponseEntity.ok(flatTypeService.getAllFlatTypes());
    }

    @PostMapping
    public ResponseEntity<FlatType> createFlatType(@RequestBody FlatType flatType) {
        return ResponseEntity.ok(flatTypeService.saveFlatType(flatType));
    }
}
