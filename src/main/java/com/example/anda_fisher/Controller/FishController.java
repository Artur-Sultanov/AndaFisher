package com.example.anda_fisher.Controller;

import com.example.anda_fisher.Model.Fish;
import com.example.anda_fisher.Service.FishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fish")
public class FishController {
    @Autowired
    private FishService fishService;

    @GetMapping
    public List<Fish> getAllFish() {
        return fishService.getAllFish();
    }

    @GetMapping("/{id}")
    public Fish getFishById(@PathVariable Long id) {
        return fishService.getFishById(id);
    }

    @PostMapping
    public Fish createFish(@RequestBody Fish fish) {
        return fishService.saveFish(fish);
    }

    @DeleteMapping("/{id}")
    public void deleteFish(@PathVariable Long id) {
        fishService.deleteFish(id);
    }
}
