package com.example.j2ee16.service.impl;

import com.example.j2ee16.dto.response.ProvinceResponse;
import com.example.j2ee16.repository.ProvinceRepository;
import com.example.j2ee16.service.ProvinceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProvinceServiceImpl implements ProvinceService {
    private final ProvinceRepository provinceRepository;

    public ProvinceServiceImpl(ProvinceRepository provinceRepository) {
        this.provinceRepository = provinceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProvinceResponse> getAllProvinces() {
        return provinceRepository.findAll().stream()
                .map(p -> new ProvinceResponse(p.getId(), p.getName(), p.getCode()))
                .collect(Collectors.toList());
    }
}
