package com.nvkproject3.repository;

import com.nvkproject3.model.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    // Tìm sản phẩm theo tên (tìm gần đúng)
    List<SanPham> findByTenContainingIgnoreCase(String ten);

    // Tìm sản phẩm theo tên với phân trang
    Page<SanPham> findByTenContainingIgnoreCase(String ten, Pageable pageable);

    // Lấy sản phẩm mới nhất
    List<SanPham> findTop12ByOrderByIdDesc();

    // Tìm theo nhà xuất bản
    List<SanPham> findByNhaXuatBan(String nhaXuatBan);

    // Tìm theo năm xuất bản
    List<SanPham> findByNamXuatBan(Integer namXuatBan);

    // Tìm sản phẩm còn hàng
    @Query("SELECT s FROM SanPham s WHERE s.soLuongTon > 0")
    List<SanPham> findInStock();

    // Tìm theo khoảng giá
    @Query("SELECT s FROM SanPham s WHERE s.gia BETWEEN :minPrice AND :maxPrice")
    List<SanPham> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Tìm sản phẩm theo danh mục (join với bảng trung gian)
    @Query("SELECT s FROM SanPham s JOIN SanPhamDanhMuc sdm ON s.id = sdm.sanPhamId WHERE sdm.danhMucId = :danhMucId")
    List<SanPham> findByDanhMucId(@Param("danhMucId") Integer danhMucId);

    // Tìm sản phẩm theo tác giả (join với bảng trung gian)
    @Query("SELECT s FROM SanPham s JOIN SanPhamTacGia stg ON s.id = stg.sanPhamId WHERE stg.tacGiaId = :tacGiaId")
    List<SanPham> findByTacGiaId(@Param("tacGiaId") Integer tacGiaId);
}