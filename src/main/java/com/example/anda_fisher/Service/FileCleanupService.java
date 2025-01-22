package com.example.anda_fisher.Service;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileCleanupService {

    private final String basePath = "C:/Users/artur/Projects/anda-fisher/uploads/images";

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
}