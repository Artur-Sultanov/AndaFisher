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

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path basePath = Paths.get("C:/Users/artur/Projects/anda-fisher/uploads/images", subDirectory);
        Path filePath = basePath.resolve(fileName);

        Files.createDirectories(filePath.getParent());

        Files.write(filePath, file.getBytes());

        return Paths.get(subDirectory, fileName).toString().replace("\\", "/");
    }
}
