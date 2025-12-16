package com.nvkproject3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "danh_muc")
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_danh_muc", nullable = false)
    private String tenDanhMuc;

    @Column(name = "danh_muc_cha_id")
    private Integer danhMucChaId;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    // Constructors
    public DanhMuc() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public Integer getDanhMucChaId() {
        return danhMucChaId;
    }

    public void setDanhMucChaId(Integer danhMucChaId) {
        this.danhMucChaId = danhMucChaId;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}