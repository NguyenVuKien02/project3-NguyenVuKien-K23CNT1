package com.nvkproject3.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "san_pham")
@Data
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ten;

    @Column(name = "ma_sku", unique = true)
    private String maSku;

    @Column(nullable = false)
    private Double gia;

    @Column(name = "so_luong_ton", columnDefinition = "INT DEFAULT 0")
    private Integer soLuongTon;

    @Column(name = "nha_xuat_ban")
    private String nhaXuatBan;

    @Column(name = "nam_xuat_ban")
    private Integer namXuatBan;

    @Column(name = "so_trang")
    private Integer soTrang;

    @Column(name = "trong_luong")
    private Double trongLuong;

    @Column(name = "ngon_ngu")
    private String ngonNgu;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "anh_dai_dien")
    private String anhDaiDien;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SanPhamAnh> anhChiTiets;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SanPhamTacGia> tacGias;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SanPhamDanhMuc> danhMucs;
}