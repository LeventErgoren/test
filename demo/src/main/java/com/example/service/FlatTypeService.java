package com.example.service;

import com.example.entity.FlatType;
import com.example.repository.FlatTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlatTypeService {

    private final FlatTypeRepository flatTypeRepository;

    public List<FlatType> getAllFlatTypes() {
        return flatTypeRepository.findAll();
    }

    public FlatType saveFlatType(FlatType flatType) {
        return flatTypeRepository.save(flatType);
    }
}
