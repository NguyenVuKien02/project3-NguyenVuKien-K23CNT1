package com.nvkproject3.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "danh_muc")
@Data
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_danh_muc", nullable = false)
    private String ten;

    @Column(name = "danh_muc_cha_id")
    private Integer danhMucChaId;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    // Self-referencing relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "danh_muc_cha_id", insertable = false, updatable = false)
    private DanhMuc danhMucCha;

    @OneToMany(mappedBy = "danhMucCha", fetch = FetchType.LAZY)
    private Set<DanhMuc> danhMucCons = new HashSet<>();

    // Many-to-Many với SanPham
    @ManyToMany(mappedBy = "danhMucs", fetch = FetchType.LAZY)
    private Set<SanPham> sanPhams = new HashSet<>();
}