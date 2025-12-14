package com.nvkproject3.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "tac_gia")
@Data
public class TacGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}