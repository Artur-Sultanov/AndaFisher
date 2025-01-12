package com.example.anda_fisher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeachDTO {
    private Long id;
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private String waterType;
    private String imagePath;
}
