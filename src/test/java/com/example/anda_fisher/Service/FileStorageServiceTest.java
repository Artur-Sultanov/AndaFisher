package com.example.anda_fisher.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;
    private FileCleanupService fileCleanupService;

    @BeforeEach
    void setUp() {
        fileCleanupService = mock(FileCleanupService.class);
        fileStorageService = new FileStorageService(fileCleanupService);
    }

    @Test
    void testSaveFile_NewFile_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getBytes()).thenReturn("test content".getBytes());

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenAnswer(invocation -> null);
            mockedFiles.when(() -> Files.write(any(Path.class), any(byte[].class))).thenAnswer(invocation -> null);

            String result = fileStorageService.saveFile(file, "beaches", null);

            assertTrue(result.contains("beaches"), "Returned path should include the sub-directory");
            mockedFiles.verify(() -> Files.createDirectories(any(Path.class)), times(1));
            mockedFiles.verify(() -> Files.write(any(Path.class), any(byte[].class)), times(1));
        }
    }

    @Test
    void testSaveFile_WithOldFilePath_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getBytes()).thenReturn("test content".getBytes());

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenAnswer(invocation -> null);
            mockedFiles.when(() -> Files.write(any(Path.class), any(byte[].class))).thenAnswer(invocation -> null);

            String result = fileStorageService.saveFile(file, "beaches", "old/test.jpg");

            assertTrue(result.contains("beaches"), "Returned path should include the sub-directory");
            verify(fileCleanupService, times(1)).deleteFile("old/test.jpg");
            mockedFiles.verify(() -> Files.createDirectories(any(Path.class)), times(1));
            mockedFiles.verify(() -> Files.write(any(Path.class), any(byte[].class)), times(1));
        }
    }

    @Test
    void testSaveFile_InvalidFile_ThrowsException() {
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileStorageService.saveFile(emptyFile, "beaches", null));

        assertEquals("Uploaded file is empty or null", exception.getMessage());
    }

    @Test
    void testSaveFile_FileCreationError() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getBytes()).thenReturn("test content".getBytes());

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.createDirectories(any(Path.class)))
                    .thenThrow(new IOException("Simulated creation error"));

            IOException exception = assertThrows(IOException.class,
                    () -> fileStorageService.saveFile(file, "beaches", null));

            assertEquals("Simulated creation error", exception.getMessage());
        }
    }
}
