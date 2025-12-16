package com.nvkproject3.repository;

import com.nvkproject3.model.SanPhamAnh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamAnhRepository extends JpaRepository<SanPhamAnh, Integer> {

    // Lấy tất cả ảnh của một sản phẩm, sắp xếp theo thứ tự
    List<SanPhamAnh> findBySanPhamIdOrderByThuTuAsc(Integer sanPhamId);

    // Xóa tất cả ảnh của sản phẩm
    void deleteBySanPhamId(Integer sanPhamId);
}