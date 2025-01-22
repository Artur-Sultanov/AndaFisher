package com.example.anda_fisher.Service;

import com.example.anda_fisher.DTO.WeatherDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherDTO getFormattedWeather(double latitude, double longitude) {
        String weatherData = getWeather(latitude, longitude);
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(weatherData);
            String location = root.get("name").asText();
            String description = root.get("weather").get(0).get("description").asText();
            double temp = root.get("main").get("temp").asDouble();
            double feelsLike = root.get("main").get("feels_like").asDouble();
            double windSpeed = root.get("wind").get("speed").asDouble();
            int humidity = root.get("main").get("humidity").asInt();
            String icon = root.get("weather").get(0).get("icon").asText();
            String iconUrl = String.format("http://openweathermap.org/img/wn/%s@2x.png", icon);

            return new WeatherDTO(location, description, temp, feelsLike, windSpeed, humidity, iconUrl);

        } catch (Exception e) {
            throw new RuntimeException("Error parsing weather data", e);
        }
    }

    /**
     * Получает "сырые" данные о погоде.
     */
    public String getWeather(double latitude, double longitude) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&appid=%s",
                latitude, longitude, apiKey
        );
        return restTemplate.getForObject(url, String.class);
    }
}
