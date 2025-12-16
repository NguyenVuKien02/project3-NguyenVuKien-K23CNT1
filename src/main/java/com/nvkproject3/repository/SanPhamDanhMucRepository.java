package com.nvkproject3.repository;

import com.nvkproject3.model.SanPhamDanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamDanhMucRepository extends JpaRepository<SanPhamDanhMuc, Integer> {

    // Lấy tất cả danh mục của một sản phẩm
    List<SanPhamDanhMuc> findBySanPhamId(Integer sanPhamId);

    // Lấy tất cả sản phẩm trong một danh mục
    List<SanPhamDanhMuc> findByDanhMucId(Integer danhMucId);

    // Xóa tất cả danh mục của sản phẩm
    void deleteBySanPhamId(Integer sanPhamId);

    // Xóa một liên kết cụ thể
    void deleteBySanPhamIdAndDanhMucId(Integer sanPhamId, Integer danhMucId);
}