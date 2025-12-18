package com.nvkproject3.controller.admin;

import com.nvkproject3.model.DonHang;
import com.nvkproject3.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    /**
     * Trang Dashboard chính
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // Thống kê tổng quan
        long tongSanPham = sanPhamRepository.count();
        long tongNguoiDung = nguoiDungRepository.count();
        long tongDonHang = donHangRepository.count();
        long tongDanhMuc = danhMucRepository.count();

        // Đơn hàng chờ xác nhận
        List<DonHang> danhSachDonHang = donHangRepository.findAll();
        long donHangChoXacNhan = danhSachDonHang.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.cho_xac_nhan)
                .count();

        // Tính tổng doanh thu
        double tongDoanhThu = danhSachDonHang.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.da_giao)
                .mapToDouble(DonHang::getTongTien)
                .sum();

        // Doanh thu hôm nay
        LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        double doanhThuHomNay = danhSachDonHang.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.da_giao)
                .filter(dh -> dh.getNgayDat().isAfter(startOfDay))
                .mapToDouble(DonHang::getTongTien)
                .sum();

        // Đơn hàng mới nhất
        List<DonHang> donHangMoiNhat = danhSachDonHang.stream()
                .sorted((d1, d2) -> d2.getNgayDat().compareTo(d1.getNgayDat()))
                .limit(5)
                .toList();

        // Add vào model
        model.addAttribute("tongSanPham", tongSanPham);
        model.addAttribute("tongNguoiDung", tongNguoiDung);
        model.addAttribute("tongDonHang", tongDonHang);
        model.addAttribute("tongDanhMuc", tongDanhMuc);
        model.addAttribute("donHangChoXacNhan", donHangChoXacNhan);
        model.addAttribute("tongDoanhThu", tongDoanhThu);
        model.addAttribute("doanhThuHomNay", doanhThuHomNay);
        model.addAttribute("donHangMoiNhat", donHangMoiNhat);

        return "admin/dashboard";
    }
}