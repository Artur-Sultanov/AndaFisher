package com.example.anda_fisher.Config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PlaceholderImageResolver implements ResourceResolver {

    private static final String UPLOAD_DIR = "uploads/images/";
    private static final String PLACEHOLDER_IMAGE = "static/images/placeholder-image.jpeg";

    @Override
    public Resource resolveResource(
            HttpServletRequest request,
            String requestPath,
            List<? extends Resource> locations,
            ResourceResolverChain chain
    ) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR, requestPath.replace("uploads/images/", ""));
            if (Files.exists(filePath)) {
                return new UrlResource(filePath.toUri());
            } else {
                return new ClassPathResource(PLACEHOLDER_IMAGE);
            }
        } catch (Exception e) {
            return new ClassPathResource(PLACEHOLDER_IMAGE);
        }
    }

    @Override
    public String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return null;
    }

}

