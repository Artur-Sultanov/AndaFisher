package com.example.anda_fisher.Controller;

import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Model.WaterType;
import com.example.anda_fisher.Service.BeachService;
import com.example.anda_fisher.Service.FileStorageService;
import com.example.anda_fisher.Service.FishService;
import com.example.anda_fisher.dto.BeachDTO;
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

    // Injecting required services
    private final BeachService beachService;
    private final FishService fishService;
    private final FileStorageService fileStorageService;

    /**
     * Retrieve a list of all beaches.
     * @return List of BeachDTO objects.
     */
    @GetMapping
    public ResponseEntity<List<BeachDTO>> getAllBeaches() {
        List<BeachDTO> beaches = beachService.getAllBeaches()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beaches);
    }

    /**
     * Retrieve a beach by its ID.
     * @param id Beach ID.
     * @return BeachDTO if found, 404 if not.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BeachDTO> getBeachById(@PathVariable Long id) {
        Beach beach = beachService.getBeachById(id);
        if (beach == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(beach));
    }

    /**
     * Create a new beach.
     * @param beachDTO Data transfer object containing beach details.
     * @return Created BeachDTO with HTTP 201 status.
     */
    @PostMapping
    public ResponseEntity<BeachDTO> createBeach(@RequestBody BeachDTO beachDTO) {
        try {

            Beach beach = convertToEntity(beachDTO);

            Beach savedBeach = beachService.saveBeach(beach);

            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedBeach));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing beach by its ID.
     * @param id Beach ID.
     * @param beachDTO Updated beach details.
     * @return Updated BeachDTO if successful.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BeachDTO> updateBeach(@PathVariable Long id, @RequestBody BeachDTO beachDTO) {
        Beach existingBeach = beachService.getBeachById(id);

        if (existingBeach == null) {
            return ResponseEntity.notFound().build();
        }

        // Updating beach fields
        existingBeach.setName(beachDTO.getName());
        existingBeach.setLocation(beachDTO.getLocation());
        existingBeach.setLatitude(beachDTO.getLatitude());
        existingBeach.setLongitude(beachDTO.getLongitude());
        existingBeach.setWaterType(WaterType.valueOf(beachDTO.getWaterType()));
        existingBeach.setImagePath(beachDTO.getImagePath());

        Beach updatedBeach = beachService.saveBeach(existingBeach);

        return ResponseEntity.ok(convertToDTO(updatedBeach));
    }


    /**
     * Delete a beach by its ID.
     * @param id Beach ID.
     * @return Success message or 404 if not found.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBeach(@PathVariable Long id) {
        Beach beach = beachService.getBeachById(id);
        if (beach == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Beach not found with id: " + id);
        }
        beachService.deleteBeach(id);
        return ResponseEntity.ok("Beach with id " + id + " has been successfully deleted.");
    }

    /**
     * Upload an image for a specific beach.
     * @param id Beach ID.
     * @param file Multipart image file.
     * @return Success or error message.
     */
    @PostMapping("/{id}/uploadImage")
    public ResponseEntity<String> uploadBeachImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Beach beach = beachService.getBeachById(id);

        if (beach == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Beach not found with id: " + id);
        }

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Uploaded file is empty or missing");
        }

        // Validate file type
        String fileName = file.getOriginalFilename();
        if (fileName != null && !fileName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
            return ResponseEntity.badRequest().body("Invalid file type. Allowed types: jpg, jpeg, png, gif.");
        }

        try {
            String imagePath = fileStorageService.saveFile(file, "beaches");
            beach.setImagePath(imagePath);
            beachService.saveBeach(beach);

            return ResponseEntity.ok("Image uploaded successfully: " + imagePath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image: " + e.getMessage());
        }
    }

    /**
     * Convert a Beach entity to a BeachDTO.
     * @param beach Beach entity.
     * @return BeachDTO object.
     */
    private BeachDTO convertToDTO(Beach beach) {
        return new BeachDTO(
                beach.getId(),
                beach.getName(),
                beach.getLocation(),
                beach.getLatitude(),
                beach.getLongitude(),
                beach.getWaterType().toString(),
                beach.getImagePath()
        );
    }

    /**
     * Convert a BeachDTO to a Beach entity.
     * @param beachDTO BeachDTO object.
     * @return Beach entity.
     */
    private Beach convertToEntity(BeachDTO beachDTO) {
        Beach beach = new Beach();
        beach.setName(beachDTO.getName());
        beach.setLocation(beachDTO.getLocation());
        beach.setLatitude(beachDTO.getLatitude());
        beach.setLongitude(beachDTO.getLongitude());
        beach.setWaterType(WaterType.valueOf(beachDTO.getWaterType()));
        beach.setImagePath(beachDTO.getImagePath());
        return beach;
    }
}