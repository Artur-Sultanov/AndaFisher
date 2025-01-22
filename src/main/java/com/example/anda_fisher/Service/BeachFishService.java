package com.example.anda_fisher.Service;

import com.example.anda_fisher.Exception.ResourceNotFoundException;
import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Model.BeachFish;
import com.example.anda_fisher.Model.Fish;
import com.example.anda_fisher.Repository.BeachRepository;
import com.example.anda_fisher.Repository.FishRepository;
import com.example.anda_fisher.Repository.BeachFishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeachFishService {

    private final BeachRepository beachRepository;
    private final FishRepository fishRepository;
    private final BeachFishRepository beachFishRepository;

    public void addFishToBeach(Long beachId, List<Long> fishIds) {
        // Проверяем существование пляжа
        Beach beach = beachRepository.findById(beachId)
                .orElseThrow(() -> new ResourceNotFoundException("Beach not found with ID: " + beachId));

        // Находим рыбу по переданным ID
        List<Fish> fishList = fishRepository.findAllById(fishIds);

        if (fishList.isEmpty()) {
            throw new IllegalArgumentException("No valid fish IDs provided.");
        }

        // Создаём связи между пляжем и рыбой
        List<BeachFish> beachFishList = fishList.stream()
                .map(fish -> new BeachFish(beach, fish))
                .toList();

        beachFishRepository.saveAll(beachFishList);
    }
}

