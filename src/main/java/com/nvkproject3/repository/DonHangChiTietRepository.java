package com.nvkproject3.repository;

import com.nvkproject3.model.DonHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangChiTietRepository extends JpaRepository<DonHangChiTiet, Integer> {

    // Lấy tất cả chi tiết của một đơn hàng
    List<DonHangChiTiet> findByDonHangId(Integer donHangId);

    // Xóa tất cả chi tiết của đơn hàng
    void deleteByDonHangId(Integer donHangId);

    // Lấy chi tiết theo sản phẩm (để xem sản phẩm nào bán chạy)
    List<DonHangChiTiet> findBySanPhamId(Integer sanPhamId);

    // Tính tổng số lượng bán của một sản phẩm
    @Query("SELECT COALESCE(SUM(d.soLuong), 0) FROM DonHangChiTiet d WHERE d.sanPhamId = :sanPhamId")
    int sumSoLuongBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    // Top sản phẩm bán chạy
    @Query("SELECT d.sanPhamId, SUM(d.soLuong) as total FROM DonHangChiTiet d GROUP BY d.sanPhamId ORDER BY total DESC")
    List<Object[]> findTopSellingProducts();
}