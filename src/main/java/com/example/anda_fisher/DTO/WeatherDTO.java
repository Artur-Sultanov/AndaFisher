package com.example.anda_fisher.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDTO {
    private String location;
    private String description;
    private double temperature;
    private double feelsLike;
    private double windSpeed;
    private int humidity;
    private String iconUrl;
}
