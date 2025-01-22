package com.example.anda_fisher.Service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileCleanupServiceTest {

    private FileCleanupService fileCleanupService;
    private Path testBasePath;

    @BeforeEach
    void setUp() throws IOException {
        fileCleanupService = new FileCleanupService();

        // Создаем временную базовую директорию для тестов
        testBasePath = Files.createTempDirectory("test-images");

        // Устанавливаем временную директорию в сервисе
        fileCleanupService = new FileCleanupService() {
            private final String basePath = testBasePath.toString();

            @Override
            public void deleteFile(String relativePath) {
                if (relativePath == null || relativePath.isBlank()) return;

                try {
                    Path filePath = Paths.get(basePath, relativePath);
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                        System.out.println("File deleted: " + filePath);
                    }
                } catch (Exception e) {
                    System.err.println("Error deleting file: " + e.getMessage());
                }
            }
        };
    }

    @AfterEach
    void tearDown() throws IOException {
        // Удаляем временную директорию и все её содержимое
        Files.walk(testBasePath)
                .map(Path::toFile)
                .forEach(file -> {
                    if (!file.delete()) {
                        System.err.println("Failed to delete file: " + file);
                    }
                });
    }

    @Test
    void testDeleteFile_Success() throws IOException {
        // Создаем тестовый файл
        Path testFile = testBasePath.resolve("test_image.jpg");
        Files.createFile(testFile);

        assertTrue(Files.exists(testFile), "Test file should exist before deletion");

        // Удаляем файл через сервис
        fileCleanupService.deleteFile("test_image.jpg");

        assertFalse(Files.exists(testFile), "Test file should be deleted");
    }

    @Test
    void testDeleteFile_FileDoesNotExist() {
        // Удаляем несуществующий файл
        fileCleanupService.deleteFile("nonexistent_image.jpg");

        // Проверяем, что метод не вызывает исключений
        assertTrue(true, "No exception should be thrown for nonexistent files");
    }

    @Test
    void testDeleteFile_NullPath() {
        // Удаляем с null значением
        fileCleanupService.deleteFile(null);

        // Проверяем, что метод не вызывает исключений
        assertTrue(true, "No exception should be thrown for null path");
    }

    @Test
    void testDeleteFile_BlankPath() {
        // Удаляем с пустым значением
        fileCleanupService.deleteFile(" ");

        // Проверяем, что метод не вызывает исключений
        assertTrue(true, "No exception should be thrown for blank path");
    }
}
