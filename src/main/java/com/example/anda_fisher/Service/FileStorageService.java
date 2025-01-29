package com.example.anda_fisher.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileCleanupService fileCleanupService;

    public String saveFile(MultipartFile file, String subDirectory, String oldFilePath) throws IOException {
        if (oldFilePath != null && !oldFilePath.isBlank()) {
            fileCleanupService.deleteFile(oldFilePath);
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or null");
        }

        String originalFileName = file.getOriginalFilename();
        String sanitizedFileName = generateSafeFileName(originalFileName);

        Path basePath = Paths.get("C:/Users/artur/Projects/anda-fisher/uploads/images", subDirectory);
        Path filePath = basePath.resolve(sanitizedFileName);

        Files.createDirectories(filePath.getParent());

        Files.write(filePath, file.getBytes());

        return Paths.get(subDirectory, sanitizedFileName).toString().replace("\\", "/");
    }

    private String generateSafeFileName(String originalFileName) {
        if (originalFileName == null) {
            throw new IllegalArgumentException("File name is null");
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        return originalFileName
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9._-]", "")
                .replace(".", "_" + timestamp + ".");
    }
}
