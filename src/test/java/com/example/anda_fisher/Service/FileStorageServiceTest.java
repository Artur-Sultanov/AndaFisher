package com.example.anda_fisher.Service;

import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileStorageServiceTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${file.upload.dir:uploads/images/}")
    private String uploadDir;

    @Test
    public void testSaveFile_Success() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "dummy content".getBytes());

        // Act
        String result = fileStorageService.saveFile(file, "testSubDir");

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("testSubDir"));
        Path path = Paths.get(uploadDir, result);
        assertTrue(Files.exists(path));

        // Cleanup
        Files.deleteIfExists(path);
    }

    @Test
    public void testSaveFile_EmptyFile() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "", "image/png", new byte[0]);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> fileStorageService.saveFile(file, "testSubDir"));
    }

    @Test
    public void testSaveFile_DirectoryCreation() throws IOException {
        // Arrange
        String subDir = "newDirectory";
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "dummy content".getBytes());
        Path dirPath = Paths.get(uploadDir, subDir);

        // Act
        String result = fileStorageService.saveFile(file, subDir);

        // Assert
        assertTrue(Files.exists(dirPath));

        // Cleanup
        Files.deleteIfExists(Paths.get(uploadDir, result));
        Files.deleteIfExists(dirPath);
    }
}
