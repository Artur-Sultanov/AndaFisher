package com.example.anda_fisher.Service;

import com.example.anda_fisher.DTO.BeachDTO;
import com.example.anda_fisher.Exception.ConflictException;
import com.example.anda_fisher.Exception.ResourceNotFoundException;
import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Model.WaterType;
import com.example.anda_fisher.Repository.BeachRepository;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BeachServiceTest {

    @Autowired
    private BeachService beachService;

    @MockBean
    private BeachRepository beachRepository;

    @Test
    public void testUpdateBeach_Success() {
        // Arrange
        Long beachId = 1L;
        Beach existingBeach = new Beach(
                beachId,
                "Beach 1",
                "Location 1",
                null, // description
                null, // createdAt
                null, // updatedAt
                null, // latitude
                null, // longitude
                WaterType.SALTWATER,
                null, // imagePath
                true,
                new HashSet<>()
        );

        BeachDTO beachDTO = new BeachDTO(
                beachId,
                "Beach 2",
                "Location 2",
                null, // latitude
                null, // longitude
                "SALTWATER",
                null, // imagePath
                "New Description",
                true
        );

        when(beachRepository.findById(beachId)).thenReturn(Optional.of(existingBeach));
        when(beachRepository.save(existingBeach)).thenReturn(existingBeach);

        // Act
        Beach updatedBeach = beachService.updateBeach(beachId, beachDTO);

        // Assert
        assertEquals("Beach 2", updatedBeach.getName());
        assertEquals("Location 2", updatedBeach.getLocation());
        assertEquals("New Description", updatedBeach.getDescription());
    }


    @Test
    public void testUpdateBeach_DuplicateNameAndLocation() {
        // Arrange
        Long beachId = 1L;
        BeachDTO beachDTO = new BeachDTO(beachId, "Duplicate Name", "Duplicate Location", null, null, "SALTWATER", null, null, true);

        when(beachRepository.findById(beachId)).thenReturn(Optional.of(new Beach()));
        when(beachRepository.existsByNameAndLocationExcludingId("Duplicate Name", "Duplicate Location", beachId)).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> beachService.updateBeach(beachId, beachDTO));
    }

    @Test
    public void testUpdateBeach_NotFound() {
        // Arrange
        Long beachId = 1L;
        BeachDTO beachDTO = new BeachDTO(beachId, "Beach 2", "Location 2", null, null, "SALTWATER", null, null, true);

        when(beachRepository.findById(beachId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> beachService.updateBeach(beachId, beachDTO));
    }
}
