package com.nvkproject3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "san_pham_tac_gia")
public class SanPhamTacGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "san_pham_id", nullable = false)
    private Integer sanPhamId;

    @Column(name = "tac_gia_id", nullable = false)
    private Integer tacGiaId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tac_gia_id", insertable = false, updatable = false)
    private TacGia tacGia;

    // Constructors
    public SanPhamTacGia() {}

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

    public Integer getTacGiaId() {
        return tacGiaId;
    }

    public void setTacGiaId(Integer tacGiaId) {
        this.tacGiaId = tacGiaId;
    }

    public TacGia getTacGia() {
        return tacGia;
    }

    public void setTacGia(TacGia tacGia) {
        this.tacGia = tacGia;
    }
}