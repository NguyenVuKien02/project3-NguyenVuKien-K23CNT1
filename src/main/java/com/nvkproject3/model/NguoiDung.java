package com.nvkproject3.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "nguoi_dung")
@Data
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(unique = true)
    private String email;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro")
    private VaiTro vaiTro = VaiTro.KHACH_HANG;

    private String sdt;

    @Column(columnDefinition = "TEXT")
    private String diaChi;

    @Column(name = "ngay_dang_ky")
    private LocalDateTime ngayDangKy = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.ACTIVE;

    public enum VaiTro {
        ADMIN, KHACH_HANG
    }

    public enum TrangThai {
        ACTIVE, INACTIVE
    }
}