package com.nvkproject3.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tac_gia")
public class TacGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_tac_gia", nullable = false)
    private String tenTacGia;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "quoc_gia")
    private String quocGia;

    @Column(name = "tieu_su", columnDefinition = "TEXT")
    private String tieuSu;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    // Constructors
    public TacGia() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenTacGia() {
        return tenTacGia;
    }

    public void setTenTacGia(String tenTacGia) {
        this.tenTacGia = tenTacGia;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getQuocGia() {
        return quocGia;
    }

    public void setQuocGia(String quocGia) {
        this.quocGia = quocGia;
    }

    public String getTieuSu() {
        return tieuSu;
    }

    public void setTieuSu(String tieuSu) {
        this.tieuSu = tieuSu;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}