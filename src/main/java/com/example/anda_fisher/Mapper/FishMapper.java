package com.example.anda_fisher.Mapper;

import com.example.anda_fisher.DTO.FishDTO;
import com.example.anda_fisher.Model.Fish;

public class FishMapper {

    /**
     * Convert Fish entity to FishDTO.
     */
    public static FishDTO toDTO(Fish fish) {
        return new FishDTO(fish.getId(), fish.getName(), fish.isApproved());
    }

    /**
     * Convert FishDTO to Fish entity.
     */
    public static Fish toEntity(FishDTO fishDTO) {
        Fish fish = new Fish();
        fish.setName(fishDTO.getName());
        fish.setApproved(fishDTO.isApproved());
        return fish;
    }
}
