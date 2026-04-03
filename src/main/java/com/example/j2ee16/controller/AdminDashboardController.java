package com.example.j2ee16.controller;

import com.example.j2ee16.dto.response.DashboardOverviewResponse;
import com.example.j2ee16.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewResponse> getOverview() {
        return ResponseEntity.ok(adminDashboardService.getDashboardOverview());
    }
}
