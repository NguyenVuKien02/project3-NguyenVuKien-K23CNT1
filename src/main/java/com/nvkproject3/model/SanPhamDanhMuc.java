package com.nvkproject3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "san_pham_danh_muc")
public class SanPhamDanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "san_pham_id", nullable = false)
    private Integer sanPhamId;

    @Column(name = "danh_muc_id", nullable = false)
    private Integer danhMucId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "danh_muc_id", insertable = false, updatable = false)
    private DanhMuc danhMuc;

    // Constructors
    public SanPhamDanhMuc() {}

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

    public Integer getDanhMucId() {
        return danhMucId;
    }

    public void setDanhMucId(Integer danhMucId) {
        this.danhMucId = danhMucId;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }
}