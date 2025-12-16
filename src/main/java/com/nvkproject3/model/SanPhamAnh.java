package com.nvkproject3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "san_pham_anh")
public class SanPhamAnh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "san_pham_id", nullable = false)
    private Integer sanPhamId;

    @Column(name = "url_anh", nullable = false)
    private String urlAnh;

    @Column(name = "thu_tu")
    private Integer thuTu = 0;

    // Constructors
    public SanPhamAnh() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(Integer sanPhamId) {
        this.sanPhamId = sanPhamId;
    }

    public String getUrlAnh() {
        return urlAnh;
    }

    public void setUrlAnh(String urlAnh) {
        this.urlAnh = urlAnh;
    }

    public Integer getThuTu() {
        return thuTu;
    }

    public void setThuTu(Integer thuTu) {
        this.thuTu = thuTu;
    }
}