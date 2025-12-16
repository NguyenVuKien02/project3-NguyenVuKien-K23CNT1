package com.nvkproject3.repository;

import com.nvkproject3.model.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {

    // Lấy tất cả sản phẩm trong giỏ hàng
    List<GioHangChiTiet> findByGioHangId(Integer gioHangId);

    // Tìm một sản phẩm cụ thể trong giỏ hàng
    Optional<GioHangChiTiet> findByGioHangIdAndSanPhamId(Integer gioHangId, Integer sanPhamId);

    // Xóa tất cả sản phẩm trong giỏ hàng
    void deleteByGioHangId(Integer gioHangId);

    // Đếm số lượng sản phẩm trong giỏ
    @Query("SELECT COUNT(g) FROM GioHangChiTiet g WHERE g.gioHangId = :gioHangId")
    long countByGioHangId(@Param("gioHangId") Integer gioHangId);

    // Tính tổng số lượng sản phẩm (sum của số lượng)
    @Query("SELECT COALESCE(SUM(g.soLuong), 0) FROM GioHangChiTiet g WHERE g.gioHangId = :gioHangId")
    int sumSoLuongByGioHangId(@Param("gioHangId") Integer gioHangId);
}