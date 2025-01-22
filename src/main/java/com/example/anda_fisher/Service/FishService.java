package com.example.anda_fisher.Service;

import com.example.anda_fisher.DTO.FishDTO;
import com.example.anda_fisher.Mapper.FishMapper;
import com.example.anda_fisher.Model.Fish;
import com.example.anda_fisher.Repository.FishRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FishService {

    private final FishRepository fishRepository;

    // Constructor-based dependency injection
    public FishService(FishRepository fishRepository) {
        this.fishRepository = fishRepository;
    }

    public List<FishDTO> getAllFish() {
        return fishRepository.findAll()
                .stream()
                .filter(Fish::isApproved)
                .map(FishMapper::toDTO)
                .collect(Collectors.toList());
    }

    public FishDTO getFishById(Long id) {
        Fish fish = fishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fish not found with id: " + id));
        return FishMapper.toDTO(fish);
    }

    public FishDTO saveFish(FishDTO fishDTO) {
        Fish fish = FishMapper.toEntity(fishDTO);
        fish.setApproved(false);
        Fish savedFish = fishRepository.save(fish);
        return FishMapper.toDTO(savedFish);
    }

    public void deleteFish(Long id) {
        if (!fishRepository.existsById(id)) {
            throw new RuntimeException("Fish not found with id: " + id);
        }
        fishRepository.deleteById(id);
    }
}