package com.nvkproject3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gio_hang_chi_tiet")
public class GioHangChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "gio_hang_id", nullable = false)
    private Integer gioHangId;

    @Column(name = "san_pham_id", nullable = false)
    private Integer sanPhamId;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 1;

    @Column(name = "ngay_them")
    private LocalDateTime ngayThem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "san_pham_id", insertable = false, updatable = false)
    private SanPham sanPham;

    // Constructors
    public GioHangChiTiet() {
        this.ngayThem = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGioHangId() {
        return gioHangId;
    }

    public void setGioHangId(Integer gioHangId) {
        this.gioHangId = gioHangId;
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

    public LocalDateTime getNgayThem() {
        return ngayThem;
    }

    public void setNgayThem(LocalDateTime ngayThem) {
        this.ngayThem = ngayThem;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    // Tính tổng tiền của item này
    public Double getTongTien() {
        if (sanPham != null && sanPham.getGia() != null) {
            return sanPham.getGia() * soLuong * 0.9; // -10% discount
        }
        return 0.0;
    }
}