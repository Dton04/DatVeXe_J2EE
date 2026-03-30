package com.example.j2ee16.service;

import com.example.j2ee16.dto.response.ProvinceResponse;
import java.util.List;

public interface ProvinceService {
    List<ProvinceResponse> getAllProvinces();
}
