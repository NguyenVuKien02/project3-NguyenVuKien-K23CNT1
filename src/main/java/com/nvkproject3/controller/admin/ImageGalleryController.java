package com.nvkproject3.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/images")
public class ImageGalleryController {

    private static final String STATIC_IMAGES_DIR = "src/main/resources/static/images/products/";
    private static final String UPLOAD_IMAGES_DIR = "uploads/products/";

    /**
     * Lấy danh sách ảnh có sẵn từ cả 2 nguồn
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listImages() {
        Map<String, Object> response = new HashMap<>();
        List<String> images = new ArrayList<>();

        try {
            System.out.println("=== List Images Started ===");

            // 1. Load ảnh từ static folder
            File staticDir = new File(STATIC_IMAGES_DIR);
            if (staticDir.exists()) {
                System.out.println("Reading static directory: " + staticDir.getAbsolutePath());
                addImagesFromDirectory(staticDir, "/images/products/", images);
            }

            // 2. Load ảnh từ uploads folder (ảnh mới upload)
            File uploadDir = new File(UPLOAD_IMAGES_DIR);
            if (uploadDir.exists()) {
                System.out.println("Reading upload directory: " + uploadDir.getAbsolutePath());
                addImagesFromDirectory(uploadDir, "/uploads/products/", images);
            } else {
                uploadDir.mkdirs();
                System.out.println("Created upload directory: " + uploadDir.getAbsolutePath());
            }

            response.put("success", true);
            response.put("images", images);
            response.put("count", images.size());

            System.out.println("=== List Images Success: " + images.size() + " images ===");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("=== List Images Error ===");
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Lỗi khi đọc thư mục: " + e.getMessage());
            response.put("images", images);
            response.put("count", 0);

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Helper method để thêm ảnh từ thư mục
     */
    private void addImagesFromDirectory(File dir, String urlPrefix, List<String> images) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String filename = file.getName();
                    if (filename.matches("(?i).*\\.(jpg|jpeg|png|gif|webp)$")) {
                        images.add(urlPrefix + filename);
                        System.out.println("Added: " + urlPrefix + filename);
                    }
                }
            }
        }
    }
}