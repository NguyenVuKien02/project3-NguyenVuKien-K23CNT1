package com.nvkproject3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "danh_gia")
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "san_pham_id", nullable = false)
    private Integer sanPhamId;

    @Column(name = "nguoi_dung_id", nullable = false)
    private Integer nguoiDungId;

    @Column(nullable = false)
    private Integer diem; // 1-5 sao

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "ngay_danh_gia")
    private LocalDateTime ngayDanhGia;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.cho_duyet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nguoi_dung_id", insertable = false, updatable = false)
    private NguoiDung nguoiDung;

    // Enum trạng thái đánh giá
    public enum TrangThai {
        cho_duyet, da_duyet, tu_choi
    }

    // Constructors
    public DanhGia() {
        this.ngayDanhGia = LocalDateTime.now();
        this.trangThai = TrangThai.cho_duyet;
    }

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

    public Integer getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(Integer nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public Integer getDiem() {
        return diem;
    }

    public void setDiem(Integer diem) {
        // Validate diem từ 1-5
        if (diem != null && diem >= 1 && diem <= 5) {
            this.diem = diem;
        }
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public LocalDateTime getNgayDanhGia() {
        return ngayDanhGia;
    }

    public void setNgayDanhGia(LocalDateTime ngayDanhGia) {
        this.ngayDanhGia = ngayDanhGia;
    }

    public TrangThai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThai trangThai) {
        this.trangThai = trangThai;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }
}