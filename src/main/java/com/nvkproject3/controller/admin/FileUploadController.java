package com.nvkproject3.controller.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/upload")
public class FileUploadController {

    // Upload vào thư mục ngoài project - KHÔNG CẦN RESTART
    private static final String UPLOAD_DIR = "uploads/products/";

    // Hoặc có thể dùng absolute path:
    // private static final String UPLOAD_DIR = "C:/bookstore/uploads/products/";

    /**
     * Upload ảnh từ máy tính
     */
    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("=== Upload Image Started ===");
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());

            // Kiểm tra file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "File trống!");
                return ResponseEntity.badRequest().body(response);
            }

            // Kiểm tra định dạng
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "Chỉ chấp nhận file ảnh!");
                return ResponseEntity.badRequest().body(response);
            }

            // Kiểm tra kích thước (max 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "File quá lớn! Tối đa 10MB");
                return ResponseEntity.badRequest().body(response);
            }

            // Tạo thư mục nếu chưa có
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                System.out.println("Directory created: " + created);
                System.out.println("Upload directory: " + uploadDir.getAbsolutePath());
            }

            // Tạo tên file unique
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // Lưu file
            Path path = Paths.get(UPLOAD_DIR + newFilename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("File saved to: " + path.toAbsolutePath());

            // Trả về URL ảnh
            String imageUrl = "/uploads/products/" + newFilename;
            response.put("success", true);
            response.put("url", imageUrl);
            response.put("message", "Upload thành công!");
            response.put("filename", newFilename);

            System.out.println("=== Upload Success ===");
            System.out.println("Image URL: " + imageUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            System.err.println("=== Upload Error ===");
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Lỗi khi upload: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}