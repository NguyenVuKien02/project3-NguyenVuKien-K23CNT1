package com.nvkproject3.service;

import com.nvkproject3.model.SanPham;
import com.nvkproject3.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final String uploadDir = "uploads/";

    public List<SanPham> getAllSanPham() {
        return sanPhamRepository.findAll();
    }

    public SanPham getSanPhamById(Long id) {
        return sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
    }

    public SanPham saveSanPham(SanPham sanPham, MultipartFile anhDaiDien) {
        if (anhDaiDien != null && !anhDaiDien.isEmpty()) {
            String fileName = saveImage(anhDaiDien);
            sanPham.setAnhDaiDien(fileName);
        }

        if (sanPham.getMaSku() == null || sanPham.getMaSku().isEmpty()) {
            sanPham.setMaSku(generateSku());
        }

        return sanPhamRepository.save(sanPham);
    }

    public void deleteSanPham(Long id) {
        sanPhamRepository.deleteById(id);
    }

    public List<SanPham> searchSanPham(String keyword) {
        return sanPhamRepository.findByTenContainingIgnoreCase(keyword);
    }

    public List<SanPham> getSanPhamByDanhMuc(Long danhMucId) {
        return sanPhamRepository.findByDanhMucId(danhMucId);
    }

    private String saveImage(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu ảnh: " + e.getMessage());
        }
    }

    private String generateSku() {
        return "SKU-" + System.currentTimeMillis();
    }
}