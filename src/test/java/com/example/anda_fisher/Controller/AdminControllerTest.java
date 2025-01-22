package com.example.anda_fisher.Controller;

import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Model.Fish;
import com.example.anda_fisher.Service.BeachService;
import com.example.anda_fisher.Service.FishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private BeachService beachService;

    @Mock
    private FishService fishService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApproveBeach() {
        // Arrange
        Beach beach = new Beach();
        beach.setId(1L);
        beach.setApproved(false);

        when(beachService.approveBeach(1L)).thenReturn(beach);

        // Act
        ResponseEntity<String> response = adminController.approveBeach(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("✅ Beach approved successfully!", response.getBody());
        verify(beachService, times(1)).approveBeach(1L);
    }

    @Test
    void testApproveFish() {
        // Arrange
        Fish fish = new Fish();
        fish.setId(1L);
        fish.setApproved(false);

        when(fishService.approveFish(1L)).thenReturn(fish);

        // Act
        ResponseEntity<String> response = adminController.approveFish(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("✅ Fish approved successfully!", response.getBody());
        verify(fishService, times(1)).approveFish(1L);
    }
}
