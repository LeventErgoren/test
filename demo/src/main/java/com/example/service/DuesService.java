package com.example.service;

import com.example.entity.Dues;
import com.example.entity.Flat;
import com.example.repository.DuesRepository;
import com.example.repository.FlatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DuesService {

    private final DuesRepository duesRepository;
    private final FlatRepository flatRepository;

    public List<Dues> getAllDues() {
        return duesRepository.findAll();
    }

    public Dues assignDuesToFlat(Long flatId, Dues dues) {
        Flat flat = flatRepository.findById(flatId)
                .orElseThrow(() -> new RuntimeException("Daire bulunamadı"));

        if (flat.isEmpty()) {
            throw new RuntimeException("Boş daireye aidat yansıtılamaz!");
        }

        boolean exists = duesRepository.existsByFlatIdAndMonthAndYear(flatId, dues.getMonth(), dues.getYear());
        if (exists) {
            throw new RuntimeException(dues.getMonth() + ". ay için aidat zaten girilmiş!");
        }

        dues.setFlat(flat);
        return duesRepository.save(dues);
    }
}
