package com.example.anda_fisher.Controller;

import com.example.anda_fisher.Service.BeachService;
import com.example.anda_fisher.Service.FishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final BeachService beachService;
    private final FishService fishService;

    @PutMapping("/beaches/{id}/approve")
    public ResponseEntity<String> approveBeach(@PathVariable Long id) {
        beachService.approveBeach(id);
        return ResponseEntity.ok("✅ Beach approved successfully!");
    }

    @PutMapping("/fish/{id}/approve")
    public ResponseEntity<String> approveFish(@PathVariable Long id) {
        fishService.approveFish(id);
        return ResponseEntity.ok("✅ Fish approved successfully!");
    }
}
