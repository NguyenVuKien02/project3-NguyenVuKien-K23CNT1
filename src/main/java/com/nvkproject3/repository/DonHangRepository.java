package com.nvkproject3.repository;

import com.nvkproject3.model.DonHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Integer> {

    // Tìm đơn hàng theo mã đơn hàng
    Optional<DonHang> findByMaDonHang(String maDonHang);

    // Lấy tất cả đơn hàng của một người dùng
    List<DonHang> findByNguoiDungIdOrderByNgayDatDesc(Integer nguoiDungId);

    // Lấy đơn hàng của người dùng với phân trang
    Page<DonHang> findByNguoiDungId(Integer nguoiDungId, Pageable pageable);

    // Tìm theo trạng thái
    List<DonHang> findByTrangThai(DonHang.TrangThai trangThai);

    // Tìm theo trạng thái với phân trang
    Page<DonHang> findByTrangThai(DonHang.TrangThai trangThai, Pageable pageable);

    // Tìm đơn hàng theo người dùng và trạng thái
    List<DonHang> findByNguoiDungIdAndTrangThai(Integer nguoiDungId, DonHang.TrangThai trangThai);

    // Tìm đơn hàng theo khoảng thời gian
    @Query("SELECT d FROM DonHang d WHERE d.ngayDat BETWEEN :startDate AND :endDate ORDER BY d.ngayDat DESC")
    List<DonHang> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Tính tổng doanh thu
    @Query("SELECT COALESCE(SUM(d.tongTien), 0) FROM DonHang d WHERE d.trangThai = :trangThai")
    Double sumTongTienByTrangThai(@Param("trangThai") DonHang.TrangThai trangThai);

    // Đếm số đơn hàng theo trạng thái
    long countByTrangThai(DonHang.TrangThai trangThai);

    // Lấy đơn hàng mới nhất
    List<DonHang> findTop10ByOrderByNgayDatDesc();

    // ============ THÊM CÁC METHOD CHO ADMIN ============

    // Tìm kiếm đơn hàng theo mã đơn hoặc SĐT
    @Query("SELECT dh FROM DonHang dh WHERE " +
            "dh.maDonHang LIKE %:keyword% OR " +
            "dh.sdtNguoiNhan LIKE %:keyword%")
    Page<DonHang> search(@Param("keyword") String keyword, Pageable pageable);

    // Tìm kiếm đơn hàng theo keyword và trạng thái
    @Query("SELECT dh FROM DonHang dh WHERE " +
            "(dh.maDonHang LIKE %:keyword% OR dh.sdtNguoiNhan LIKE %:keyword%) " +
            "AND dh.trangThai = :trangThai")
    Page<DonHang> searchByStatus(
            @Param("keyword") String keyword,
            @Param("trangThai") DonHang.TrangThai trangThai,
            Pageable pageable
    );
}