package com.nvkproject3.repository;

import com.nvkproject3.model.SanPhamTacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamTacGiaRepository extends JpaRepository<SanPhamTacGia, Integer> {

    // Lấy tất cả tác giả của một sản phẩm
    List<SanPhamTacGia> findBySanPhamId(Integer sanPhamId);

    // Lấy tất cả sản phẩm của một tác giả
    List<SanPhamTacGia> findByTacGiaId(Integer tacGiaId);

    // Xóa tất cả tác giả của sản phẩm
    void deleteBySanPhamId(Integer sanPhamId);

    // Xóa một liên kết cụ thể
    void deleteBySanPhamIdAndTacGiaId(Integer sanPhamId, Integer tacGiaId);
}