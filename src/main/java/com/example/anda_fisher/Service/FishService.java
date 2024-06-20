package com.example.anda_fisher.Service;

import com.example.anda_fisher.Model.Fish;
import com.example.anda_fisher.Repository.FishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FishService {
    @Autowired
    private FishRepository fishRepository;

    public List<Fish> getAllFish() {
        return fishRepository.findAll();
    }

    public Fish getFishById(Long id) {
        return fishRepository.findById(id).orElse(null);
    }

    public Fish saveFish(Fish fish) {
        return fishRepository.save(fish);
    }

    public void deleteFish(Long id) {
        fishRepository.deleteById(id);
    }
}