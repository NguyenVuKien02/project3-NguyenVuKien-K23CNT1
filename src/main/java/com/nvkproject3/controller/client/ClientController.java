package com.nvkproject3.controller.client;

import com.nvkproject3.model.*;
import com.nvkproject3.repository.*;
import com.nvkproject3.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private GioHangService gioHangService;

    // Thêm data chung cho tất cả trang
    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            Integer nguoiDungId = gioHangService.layNguoiDungId(authentication);
            if (nguoiDungId != null) {
                int soLuongGioHang = gioHangService.demSoLuongSanPham(nguoiDungId);
                model.addAttribute("soLuongGioHang", soLuongGioHang);
            }
        }
        List<DanhMuc> danhMucList = danhMucRepository.findByDanhMucChaIdIsNull();
        model.addAttribute("danhMucList", danhMucList);
    }

    // Trang chủ
    @GetMapping("/home")
    public String home(Model model) {
        List<SanPham> sanPhamMoi = sanPhamRepository.findTop12ByOrderByIdDesc();
        model.addAttribute("sanPhamMoi", sanPhamMoi);
        return "client/home";
    }

    // Chi tiết sản phẩm
    @GetMapping("/san-pham/{id}")
    public String chiTietSanPham(@PathVariable Integer id, Model model) {
        SanPham sanPham = sanPhamRepository.findById(id).orElse(null);
        model.addAttribute("sanPham", sanPham);

        // Lấy sản phẩm liên quan (cùng NXB)
        if (sanPham != null) {
            List<SanPham> sanPhamLienQuan = sanPhamRepository.findTop12ByOrderByIdDesc()
                    .stream()
                    .filter(sp -> !sp.getId().equals(id))
                    .limit(4)
                    .toList();
            model.addAttribute("sanPhamLienQuan", sanPhamLienQuan);
        }

        return "client/chi-tiet-san-pham";
    }

    // Danh mục
    @GetMapping("/danh-muc/{id}")
    public String danhMuc(@PathVariable Integer id, Model model) {
        DanhMuc danhMuc = danhMucRepository.findById(id).orElse(null);
        model.addAttribute("danhMuc", danhMuc);

        // Lấy tất cả sản phẩm (demo - cần join với bảng san_pham_danh_muc)
        List<SanPham> sanPhamList = sanPhamRepository.findTop12ByOrderByIdDesc();
        model.addAttribute("sanPhamList", sanPhamList);

        return "client/danh-muc";
    }

    // Tìm kiếm
    @GetMapping("/tim-kiem")
    public String timKiem(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 12);
        Page<SanPham> sanPhamPage = sanPhamRepository.findByTenContainingIgnoreCase(keyword, pageable);

        model.addAttribute("sanPhamPage", sanPhamPage);
        model.addAttribute("keyword", keyword);

        return "client/tim-kiem";
    }

    // Giỏ hàng
    @GetMapping("/gio-hang")
    public String gioHang(Model model, Authentication authentication) {
        Integer nguoiDungId = gioHangService.layNguoiDungId(authentication);
        if (nguoiDungId != null) {
            List<com.nvkproject3.model.GioHangChiTiet> danhSachSanPham = gioHangService.layDanhSachSanPham(nguoiDungId);
            Double tongTien = gioHangService.tinhTongTien(nguoiDungId);

            model.addAttribute("danhSachSanPham", danhSachSanPham);
            model.addAttribute("tongTien", tongTien);
        }
        return "client/gio-hang";
    }

    // Thêm vào giỏ hàng
    @PostMapping("/gio-hang/them")
    public String themGioHang(
            @RequestParam Integer sanPhamId,
            @RequestParam(defaultValue = "1") Integer soLuong,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Integer nguoiDungId = gioHangService.layNguoiDungId(authentication);
        if (nguoiDungId != null) {
            gioHangService.themSanPham(nguoiDungId, sanPhamId, soLuong);
            redirectAttributes.addFlashAttribute("message", "Đã thêm sản phẩm vào giỏ hàng!");
        }
        return "redirect:/client/gio-hang";
    }

    // Cập nhật số lượng trong giỏ
    @PostMapping("/gio-hang/cap-nhat")
    public String capNhatGioHang(
            @RequestParam Integer chiTietId,
            @RequestParam Integer soLuong,
            RedirectAttributes redirectAttributes) {

        gioHangService.capNhatSoLuong(chiTietId, soLuong);
        redirectAttributes.addFlashAttribute("message", "Đã cập nhật giỏ hàng!");
        return "redirect:/client/gio-hang";
    }

    // Xóa khỏi giỏ hàng
    @PostMapping("/gio-hang/xoa")
    public String xoaGioHang(
            @RequestParam Integer chiTietId,
            RedirectAttributes redirectAttributes) {

        gioHangService.xoaSanPham(chiTietId);
        redirectAttributes.addFlashAttribute("message", "Đã xóa sản phẩm khỏi giỏ hàng!");
        return "redirect:/client/gio-hang";
    }

    // Trang thanh toán
    @GetMapping("/thanh-toan")
    public String thanhToan(Model model, Authentication authentication) {
        Integer nguoiDungId = gioHangService.layNguoiDungId(authentication);
        if (nguoiDungId != null) {
            List<com.nvkproject3.model.GioHangChiTiet> danhSachSanPham = gioHangService.layDanhSachSanPham(nguoiDungId);
            Double tongTien = gioHangService.tinhTongTien(nguoiDungId);

            model.addAttribute("danhSachSanPham", danhSachSanPham);
            model.addAttribute("tongTien", tongTien);
        }
        return "client/thanh-toan";
    }
}