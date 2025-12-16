package com.nvkproject3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "don_hang_chi_tiet")
public class DonHangChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "don_hang_id", nullable = false)
    private Integer donHangId;

    @Column(name = "san_pham_id", nullable = false)
    private Integer sanPhamId;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia", nullable = false)
    private Double donGia; // Giá tại thời điểm mua

    // thanh_tien sẽ được tính tự động trong database (GENERATED ALWAYS AS)
    // Nhưng có thể tính thủ công trong Java
    @Transient
    private Double thanhTien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "san_pham_id", insertable = false, updatable = false)
    private SanPham sanPham;

    // Constructors
    public DonHangChiTiet() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDonHangId() {
        return donHangId;
    }

    public void setDonHangId(Integer donHangId) {
        this.donHangId = donHangId;
    }

    public Integer getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(Integer sanPhamId) {
        this.sanPhamId = sanPhamId;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public Double getDonGia() {
        return donGia;
    }

    public void setDonGia(Double donGia) {
        this.donGia = donGia;
    }

    public Double getThanhTien() {
        // Tính thành tiền = số lượng * đơn giá
        return soLuong != null && donGia != null ? soLuong * donGia : 0.0;
    }

    public void setThanhTien(Double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }
}