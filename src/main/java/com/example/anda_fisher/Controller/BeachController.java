package com.example.anda_fisher.Controller;

import com.example.anda_fisher.DTO.BeachDTO;
import com.example.anda_fisher.DTO.WeatherDTO;
import com.example.anda_fisher.Filter.BeachFilter;
import com.example.anda_fisher.Mapper.BeachMapper;
import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/beaches")
public class BeachController {

    private final BeachService beachService;
    private final FishService fishService;
    private final BeachFishService beachFishService;
    private final FileStorageService fileStorageService;
    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<List<BeachDTO>> getAllBeaches() {
        List<BeachDTO> beaches = beachService.getAllBeaches()
                .stream()
                .map(BeachMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beaches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeachDTO> getBeachById(@PathVariable Long id) {
        Beach beach = beachService.getBeachById(id);
        return ResponseEntity.ok(BeachMapper.toDTO(beach));
    }

    @PostMapping
    public ResponseEntity<BeachDTO> createBeach(@RequestBody @Valid BeachDTO beachDTO) {
        Beach beach = BeachMapper.toEntity(beachDTO);
        Beach savedBeach = beachService.addBeach(beach);
        return ResponseEntity.status(HttpStatus.CREATED).body(BeachMapper.toDTO(savedBeach));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeachDTO> updateBeach(@PathVariable Long id, @RequestBody BeachDTO beachDTO) {
        Beach updatedBeach = beachService.updateBeach(id, beachDTO);
        return ResponseEntity.ok(BeachMapper.toDTO(updatedBeach));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBeach(@PathVariable Long id) {
        beachService.deleteBeach(id);
        return ResponseEntity.ok("✅ Beach with ID " + id + " has been successfully deleted.");
    }

    @PostMapping("/{id}/uploadImage")
    public ResponseEntity<String> uploadBeachImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Beach beach = beachService.getBeachById(id);

        if (beach == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Beach not found with ID: " + id);
        }

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("⚠️ Uploaded file is empty or missing.");
        }
        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("⚠️ File size exceeds 5MB limit.");
        }

        // Validate file type
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
            return ResponseEntity.badRequest().body("⚠️ Invalid file type. Allowed types: jpg, jpeg, png, gif.");
        }

        try {
            String imagePath = fileStorageService.saveFile(file, "beaches", beach.getImagePath());
            beach.setImagePath(imagePath);
            beachService.updateBeach(beach.getId(), BeachMapper.toDTO(beach)); // Используем обновление

            return ResponseEntity.ok("✅ Image uploaded successfully: " + imagePath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error uploading image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/map")
    public ResponseEntity<List<BeachDTO>> getAllBeachesForMap() {
        List<BeachDTO> beaches = beachService.getAllBeaches()
                .stream()
                .map(BeachMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beaches);
    }

    @GetMapping("/map/filter")
    public ResponseEntity<List<BeachDTO>> filterBeachesForMap(BeachFilter filter) {
        List<BeachDTO> beaches = beachService.filterBeaches(filter)
                .stream()
                .filter(beach -> beach.getLatitude() != null && beach.getLongitude() != null) // Только пляжи с координатами
                .map(BeachMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beaches);
    }

    @GetMapping("/{id}/weather")
    public ResponseEntity<?> getBeachWeather(@PathVariable Long id) {
        // Проверка существования пляжа
        Beach beach = beachService.getBeachById(id);
        if (beach == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Beach not found with ID: " + id);
        }

        try {
            // Получаем отформатированные данные о погоде
            WeatherDTO weather = weatherService.getFormattedWeather(beach.getLatitude(), beach.getLongitude());
            return ResponseEntity.ok(weather);

        } catch (RuntimeException e) {
            // Обработка ошибок от внешнего API
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching weather data: " + e.getMessage());
        }
    }

    @PostMapping("/beaches/{beachId}/fish")
    public ResponseEntity<String> addFishToBeach(@PathVariable Long beachId, @RequestBody List<Long> fishIds) {
        beachFishService.addFishToBeach(beachId, fishIds);
        return ResponseEntity.ok("Fish successfully added to the beach.");
    }

}