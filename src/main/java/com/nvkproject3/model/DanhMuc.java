package com.nvkproject3.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "danh_muc")
@Getter
@Setter
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

    // QUAN TRỌNG: Override equals và hashCode chỉ dùng ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DanhMuc)) return false;
        DanhMuc danhMuc = (DanhMuc) o;
        return id != null && Objects.equals(id, danhMuc.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}