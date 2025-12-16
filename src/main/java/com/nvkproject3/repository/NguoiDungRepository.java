package com.nvkproject3.repository;

import com.nvkproject3.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {

    // Tìm người dùng theo email
    Optional<NguoiDung> findByEmail(String email);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Tìm theo vai trò
    java.util.List<NguoiDung> findByVaiTro(NguoiDung.VaiTro vaiTro);

    // Tìm theo trạng thái
    java.util.List<NguoiDung> findByTrangThai(NguoiDung.TrangThai trangThai);
}