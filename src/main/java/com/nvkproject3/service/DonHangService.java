package com.nvkproject3.service;

import com.nvkproject3.model.*;
import com.nvkproject3.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class DonHangService {

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private DonHangChiTietRepository donHangChiTietRepository;

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    /**
     * Tạo đơn hàng từ giỏ hàng
     */
    @Transactional
    public DonHang taoDonHang(Integer nguoiDungId, String diaChiGiaoHang,
                              String sdtNguoiNhan, String phuongThucThanhToan,
                              String ghiChu) {

        // 1. Lấy danh sách sản phẩm trong giỏ hàng
        List<GioHangChiTiet> danhSachSanPham = gioHangService.layDanhSachSanPham(nguoiDungId);

        if (danhSachSanPham == null || danhSachSanPham.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        // 2. Tính tổng tiền
        Double tongTien = gioHangService.tinhTongTien(nguoiDungId);

        // 3. Tạo đơn hàng
        DonHang donHang = new DonHang();
        donHang.setMaDonHang(taoMaDonHang());
        donHang.setNguoiDungId(nguoiDungId);
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setTongTien(tongTien);
        donHang.setTrangThai(DonHang.TrangThai.cho_xac_nhan);
        donHang.setDiaChiGiaoHang(diaChiGiaoHang);
        donHang.setSdtNguoiNhan(sdtNguoiNhan);
        donHang.setPhuongThucThanhToan(phuongThucThanhToan);
        donHang.setGhiChu(ghiChu);

        // Lưu đơn hàng
        donHang = donHangRepository.save(donHang);

        // 4. Tạo chi tiết đơn hàng
        for (GioHangChiTiet item : danhSachSanPham) {
            DonHangChiTiet chiTiet = new DonHangChiTiet();
            chiTiet.setDonHangId(donHang.getId());
            chiTiet.setSanPhamId(item.getSanPhamId());
            chiTiet.setSoLuong(item.getSoLuong());
            chiTiet.setDonGia(item.getSanPham().getGia() * 0.9); // Giá sau giảm 10%

            donHangChiTietRepository.save(chiTiet);

            // 5. Trừ tồn kho
            SanPham sanPham = item.getSanPham();
            sanPham.setSoLuongTon(sanPham.getSoLuongTon() - item.getSoLuong());
            sanPhamRepository.save(sanPham);
        }

        // 6. Xóa giỏ hàng
        gioHangService.xoaToanBoGioHang(nguoiDungId);

        return donHang;
    }

    /**
     * Tạo mã đơn hàng duy nhất
     */
    private String taoMaDonHang() {
        // Format: DH + YYYYMMDD + 6 số random
        // Ví dụ: DH20241217123456
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String ngay = LocalDateTime.now().format(formatter);

        Random random = new Random();
        int soRandom = 100000 + random.nextInt(900000); // 6 chữ số

        return "DH" + ngay + soRandom;
    }

    /**
     * Lấy danh sách đơn hàng của người dùng
     */
    public List<DonHang> layDanhSachDonHang(Integer nguoiDungId) {
        return donHangRepository.findByNguoiDungIdOrderByNgayDatDesc(nguoiDungId);
    }

    /**
     * Lấy chi tiết đơn hàng
     */
    public DonHang layChiTietDonHang(Integer donHangId) {
        return donHangRepository.findById(donHangId).orElse(null);
    }

    /**
     * Lấy danh sách chi tiết đơn hàng
     */
    public List<DonHangChiTiet> layDanhSachChiTiet(Integer donHangId) {
        return donHangChiTietRepository.findByDonHangId(donHangId);
    }

    /**
     * Hủy đơn hàng
     */
    @Transactional
    public boolean huyDonHang(Integer donHangId, Integer nguoiDungId) {
        DonHang donHang = donHangRepository.findById(donHangId).orElse(null);

        if (donHang == null || !donHang.getNguoiDungId().equals(nguoiDungId)) {
            return false;
        }

        // Chỉ cho phép hủy đơn hàng ở trạng thái chờ xác nhận
        if (donHang.getTrangThai() != DonHang.TrangThai.cho_xac_nhan) {
            return false;
        }

        // Hoàn lại tồn kho
        List<DonHangChiTiet> chiTietList = donHangChiTietRepository.findByDonHangId(donHangId);
        for (DonHangChiTiet chiTiet : chiTietList) {
            SanPham sanPham = sanPhamRepository.findById(chiTiet.getSanPhamId()).orElse(null);
            if (sanPham != null) {
                sanPham.setSoLuongTon(sanPham.getSoLuongTon() + chiTiet.getSoLuong());
                sanPhamRepository.save(sanPham);
            }
        }

        // Cập nhật trạng thái đơn hàng
        donHang.setTrangThai(DonHang.TrangThai.da_huy);
        donHangRepository.save(donHang);

        return true;
    }

    /**
     * Lấy ID người dùng từ Authentication
     */
    public Integer layNguoiDungId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        String email = authentication.getName();
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email).orElse(null);

        return nguoiDung != null ? nguoiDung.getId() : null;
    }

    /**
     * Đếm số đơn hàng chờ xác nhận
     */
    public int demDonHangChoXacNhan(Integer nguoiDungId) {
        List<DonHang> danhSach = donHangRepository.findByNguoiDungIdOrderByNgayDatDesc(nguoiDungId);
        return (int) danhSach.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.cho_xac_nhan)
                .count();
    }
}