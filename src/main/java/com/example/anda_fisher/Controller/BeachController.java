package com.example.anda_fisher.Controller;

import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Model.Fish;
import com.example.anda_fisher.Service.BeachService;
import com.example.anda_fisher.Service.FishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beaches")
public class BeachController {
    @Autowired
    private BeachService beachService;

    @Autowired
    private FishService fishService;

    @GetMapping
    public List<Beach> getAllBeaches() {
        return beachService.getAllBeaches();
    }

    @GetMapping("/{id}")
    public Beach getBeachById(@PathVariable Long id) {
        return beachService.getBeachById(id);
    }

    @PostMapping
    public Beach createBeach(@RequestBody Beach beach) {
        return beachService.saveBeach(beach);
    }

    @DeleteMapping("/{id}")
    public void deleteBeach(@PathVariable Long id) {
        beachService.deleteBeach(id);
    }

    @PostMapping("/{beachId}/fish/{fishId}")
    public Beach addFishToBeach(@PathVariable Long beachId, @PathVariable Long fishId) {
        Beach beach = beachService.getBeachById(beachId);
        Fish fish = fishService.getFishById(fishId);
        beach.getFish().add(fish);
        return beachService.saveBeach(beach);
    }
}