package com.example.anda_fisher.Mapper;

import com.example.anda_fisher.DTO.BeachDTO;
import com.example.anda_fisher.Model.Beach;

public class BeachMapper {

    public static BeachDTO toDTO(Beach beach) {
        return new BeachDTO(
                beach.getId(),
                beach.getName(),
                beach.getLocation(),
                beach.getLatitude(),
                beach.getLongitude(),
                beach.getWaterType().toString(),
                beach.getImagePath(),
                beach.getDescription(),
                beach.isApproved()
        );
    }

    public static Beach toEntity(BeachDTO beachDTO) {
        Beach beach = new Beach();
        beach.setName(beachDTO.getName());
        beach.setLocation(beachDTO.getLocation());
        beach.setLatitude(beachDTO.getLatitude());
        beach.setLongitude(beachDTO.getLongitude());
        beach.setWaterType(Enum.valueOf(com.example.anda_fisher.Model.WaterType.class, beachDTO.getWaterType()));
        beach.setImagePath(beachDTO.getImagePath());
        beach.setDescription(beachDTO.getDescription());
        beach.setApproved(beachDTO.isApproved());
        return beach;
    }

    // Новый метод для обновления существующего объекта
    public static void updateEntity(Beach existingBeach, BeachDTO beachDTO) {
        existingBeach.setName(beachDTO.getName());
        existingBeach.setLocation(beachDTO.getLocation());
        existingBeach.setLatitude(beachDTO.getLatitude());
        existingBeach.setLongitude(beachDTO.getLongitude());
        existingBeach.setWaterType(Enum.valueOf(com.example.anda_fisher.Model.WaterType.class, beachDTO.getWaterType()));
        existingBeach.setImagePath(beachDTO.getImagePath());
        existingBeach.setDescription(beachDTO.getDescription());
    }
}