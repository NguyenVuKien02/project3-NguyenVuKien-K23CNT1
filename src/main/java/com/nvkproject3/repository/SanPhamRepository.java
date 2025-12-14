package com.nvkproject3.repository;

import com.nvkproject3.model.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {
    List<SanPham> findByTenContainingIgnoreCase(String ten);

    @Query("SELECT sp FROM SanPham sp JOIN sp.danhMucs dm WHERE dm.danhMuc.id = :danhMucId")
    List<SanPham> findByDanhMucId(@Param("danhMucId") Long danhMucId);

    List<SanPham> findByGiaBetween(Double minPrice, Double maxPrice);

    SanPham findByMaSku(String maSku);
}