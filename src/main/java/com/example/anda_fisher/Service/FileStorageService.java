package com.example.anda_fisher.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String uploadDir = "uploads/images/";

    public String saveFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or null");
        }
        String directoryPath = uploadDir + subDir;
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path filePath = Paths.get(directoryPath, fileName);

        Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/images/" + subDir + "/" + fileName;
    }
}
