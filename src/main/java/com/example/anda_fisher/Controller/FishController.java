package com.example.anda_fisher.Controller;

import com.example.anda_fisher.DTO.FishDTO;
import com.example.anda_fisher.Service.FishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fish")
public class FishController {

    private final FishService fishService;

    // Constructor injection
    public FishController(FishService fishService) {
        this.fishService = fishService;
    }

    @GetMapping
    public ResponseEntity<List<FishDTO>> getAllFish() {
        return ResponseEntity.ok(fishService.getAllFish());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FishDTO> getFishById(@PathVariable Long id) {
        return ResponseEntity.ok(fishService.getFishById(id));
    }

    @PostMapping
    public ResponseEntity<FishDTO> createFish(@RequestBody FishDTO fishDTO) {
        FishDTO createdFish = fishService.saveFish(fishDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFish);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFish(@PathVariable Long id) {
        try {
            FishDTO fishToDelete = fishService.getFishById(id);

            fishService.deleteFish(id);

            String responseMessage = String.format("Fish with ID %d and Name '%s' was successfully deleted.",
                    fishToDelete.getId(),
                    fishToDelete.getName());
            return ResponseEntity.ok(responseMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fish not found with id: " + id);
        }
    }
}
