package com.nvkproject3.repository;

import com.nvkproject3.model.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    // Tìm giỏ hàng theo người dùng
    Optional<GioHang> findByNguoiDungId(Integer nguoiDungId);

    // Xóa giỏ hàng của người dùng
    void deleteByNguoiDungId(Integer nguoiDungId);
}