package com.nvkproject3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "san_pham")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String ten;

    @Column(name = "ma_sku", unique = true)
    private String maSku;

    private Double gia;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon = 0;

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

    // Constructors
    public SanPham() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMaSku() {
        return maSku;
    }

    public void setMaSku(String maSku) {
        this.maSku = maSku;
    }

    public Double getGia() {
        return gia;
    }

    public void setGia(Double gia) {
        this.gia = gia;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getNhaXuatBan() {
        return nhaXuatBan;
    }

    public void setNhaXuatBan(String nhaXuatBan) {
        this.nhaXuatBan = nhaXuatBan;
    }

    public Integer getNamXuatBan() {
        return namXuatBan;
    }

    public void setNamXuatBan(Integer namXuatBan) {
        this.namXuatBan = namXuatBan;
    }

    public Integer getSoTrang() {
        return soTrang;
    }

    public void setSoTrang(Integer soTrang) {
        this.soTrang = soTrang;
    }

    public Double getTrongLuong() {
        return trongLuong;
    }

    public void setTrongLuong(Double trongLuong) {
        this.trongLuong = trongLuong;
    }

    public String getNgonNgu() {
        return ngonNgu;
    }

    public void setNgonNgu(String ngonNgu) {
        this.ngonNgu = ngonNgu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }
}