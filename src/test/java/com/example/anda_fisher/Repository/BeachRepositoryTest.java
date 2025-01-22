package com.example.anda_fisher.Repository;

import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Model.WaterType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BeachRepositoryTest {

    @Autowired
    private BeachRepository beachRepository;

    @Test
    public void testExistsByNameAndLocation_Success() {
        // Arrange
        Beach beach = new Beach(
                null, // id
                "Test Beach", // name
                "Test Location", // location
                null, // description
                null, // createdAt
                null, // updatedAt
                null, // latitude
                null, // longitude
                WaterType.SALTWATER, // waterType
                null, // imagePath
                true, // approved
                new HashSet<>() // beachFish
        );
        beachRepository.save(beach);

        // Act
        boolean exists = beachRepository.existsByNameAndLocation("Test Beach", "Test Location");

        // Assert
        assertTrue(exists);
    }


    @Test
    public void testExistsByNameAndLocationExcludingId_Success() {
        // Arrange
        Beach beach = new Beach(
                null, // id
                "Unique Beach", // name
                "Unique Location", // location
                null, // description
                null, // createdAt
                null, // updatedAt
                null, // latitude
                null, // longitude
                WaterType.SALTWATER, // waterType
                null, // imagePath
                true, // approved
                new HashSet<>() // beachFish
        );
        beach = beachRepository.save(beach);

        // Act
        boolean exists = beachRepository.existsByNameAndLocationExcludingId("Unique Beach", "Unique Location", beach.getId());

        // Assert
        assertFalse(exists);
    }

}
