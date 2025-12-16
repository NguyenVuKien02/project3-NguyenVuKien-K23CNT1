package com.nvkproject3.repository;

import com.nvkproject3.model.DanhGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {

    // Lấy tất cả đánh giá của một sản phẩm
    List<DanhGia> findBySanPhamIdOrderByNgayDanhGiaDesc(Integer sanPhamId);

    // Lấy đánh giá của sản phẩm với phân trang
    Page<DanhGia> findBySanPhamId(Integer sanPhamId, Pageable pageable);

    // Lấy đánh giá đã duyệt của sản phẩm
    List<DanhGia> findBySanPhamIdAndTrangThaiOrderByNgayDanhGiaDesc(Integer sanPhamId, DanhGia.TrangThai trangThai);

    // Lấy đánh giá của người dùng
    List<DanhGia> findByNguoiDungIdOrderByNgayDanhGiaDesc(Integer nguoiDungId);

    // Tìm đánh giá cụ thể của user cho sản phẩm (để check đã đánh giá chưa)
    Optional<DanhGia> findBySanPhamIdAndNguoiDungId(Integer sanPhamId, Integer nguoiDungId);

    // Lấy đánh giá theo trạng thái
    List<DanhGia> findByTrangThai(DanhGia.TrangThai trangThai);

    // Lấy đánh giá theo trạng thái với phân trang
    Page<DanhGia> findByTrangThai(DanhGia.TrangThai trangThai, Pageable pageable);

    // Tính điểm trung bình của sản phẩm
    @Query("SELECT COALESCE(AVG(d.diem), 0) FROM DanhGia d WHERE d.sanPhamId = :sanPhamId AND d.trangThai = :trangThai")
    Double avgDiemBySanPhamIdAndTrangThai(@Param("sanPhamId") Integer sanPhamId, @Param("trangThai") DanhGia.TrangThai trangThai);

    // Đếm số đánh giá của sản phẩm
    long countBySanPhamIdAndTrangThai(Integer sanPhamId, DanhGia.TrangThai trangThai);

    // Đếm theo số sao (để hiển thị phân bố đánh giá)
    @Query("SELECT d.diem, COUNT(d) FROM DanhGia d WHERE d.sanPhamId = :sanPhamId AND d.trangThai = :trangThai GROUP BY d.diem ORDER BY d.diem DESC")
    List<Object[]> countByDiemGrouping(@Param("sanPhamId") Integer sanPhamId, @Param("trangThai") DanhGia.TrangThai trangThai);
}