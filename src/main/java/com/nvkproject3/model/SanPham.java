package com.nvkproject3.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "san_pham")
@Data
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String ten;

    @Column(name = "ma_sku", unique = true)
    private String maSku;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @Column(nullable = false)
    private Double gia;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon = 0;

    @Column(name = "anh_dai_dien")
    private String anhDaiDien;

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

    // KHÔNG CÓ: ngayTao, ngayCapNhat, trangThai trong database gốc
    // Nếu muốn thêm, phải ALTER TABLE trước

    // Many-to-Many với DanhMuc qua bảng trung gian
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "san_pham_danh_muc",
            joinColumns = @JoinColumn(name = "san_pham_id"),
            inverseJoinColumns = @JoinColumn(name = "danh_muc_id")
    )
    private Set<DanhMuc> danhMucs = new HashSet<>();

    // Transient field - không map vào database
    // Dùng để hiển thị trạng thái trong code
    @Transient
    public String getTrangThaiDisplay() {
        if (soLuongTon == null || soLuongTon <= 0) {
            return "Hết hàng";
        } else if (soLuongTon <= 5) {
            return "Sắp hết";
        } else {
            return "Còn hàng";
        }
    }

    // Helper methods
    public void themDanhMuc(DanhMuc danhMuc) {
        this.danhMucs.add(danhMuc);
        danhMuc.getSanPhams().add(this);
    }

    public void xoaDanhMuc(DanhMuc danhMuc) {
        this.danhMucs.remove(danhMuc);
        danhMuc.getSanPhams().remove(this);
    }
}