package com.example.anda_fisher.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class FileCleanupServiceTest {

    private FileCleanupService fileCleanupService;

    @BeforeEach
    void setUp() {
        fileCleanupService = new FileCleanupService();
    }

    @Test
    void testDeleteFile_Success() {
        String relativePath = "test_image.jpg";
        Path mockPath = Paths.get("C:/Users/artur/Projects/anda-fisher/uploads/images/beaches", relativePath);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(mockPath)).thenReturn(true);
            mockedFiles.when(() -> Files.delete(mockPath)).thenAnswer(invocation -> null);

            assertDoesNotThrow(() -> fileCleanupService.deleteFile(relativePath),
                    "Deleting an existing file should not throw exceptions");

            mockedFiles.verify(() -> Files.exists(mockPath), times(1));
            mockedFiles.verify(() -> Files.delete(mockPath), times(1));
        }
    }

    @Test
    void testDeleteFile_FileDoesNotExist() {
        String relativePath = "nonexistent_image.jpg";
        Path mockPath = Paths.get("C:/Users/artur/Projects/anda-fisher/uploads/images/beaches", relativePath);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(mockPath)).thenReturn(false);

            assertDoesNotThrow(() -> fileCleanupService.deleteFile(relativePath),
                    "Attempting to delete a nonexistent file should not throw exceptions");

            mockedFiles.verify(() -> Files.exists(mockPath), times(1));
            mockedFiles.verify(() -> Files.delete(mockPath), never());
        }
    }

    @Test
    void testDeleteFile_NullOrBlankPath() {
        assertDoesNotThrow(() -> fileCleanupService.deleteFile(null),
                "Null path should not throw exceptions");
        assertDoesNotThrow(() -> fileCleanupService.deleteFile(""),
                "Blank path should not throw exceptions");
        assertDoesNotThrow(() -> fileCleanupService.deleteFile("   "),
                "Whitespace-only path should not throw exceptions");
    }

    @Test
    void testDeleteFile_ExceptionDuringDeletion() {
        String relativePath = "error_image.jpg";
        Path mockPath = Paths.get("C:/Users/artur/Projects/anda-fisher/uploads/images/beaches", relativePath);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(mockPath)).thenReturn(true);
            mockedFiles.when(() -> Files.delete(mockPath)).thenThrow(new RuntimeException("Simulated deletion error"));

            assertDoesNotThrow(() -> fileCleanupService.deleteFile(relativePath),
                    "Exceptions during deletion should not propagate to the caller");

            mockedFiles.verify(() -> Files.exists(mockPath), times(1));
            mockedFiles.verify(() -> Files.delete(mockPath), times(1));
        }
    }
}
