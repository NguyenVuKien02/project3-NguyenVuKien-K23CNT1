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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

import com.nvkproject3.service.DonHangService;
import com.nvkproject3.repository.NguoiDungRepository;
@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private DonHangService donHangService;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    // Thêm data chung cho tất cả trang
    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            Integer nguoiDungId = gioHangService.layNguoiDungId(authentication);
            if (nguoiDungId != null) {
                int soLuongGioHang = gioHangService.demSoLuongSanPham(nguoiDungId);
                model.addAttribute("soLuongGioHang", soLuongGioHang);

                // Thêm đếm đơn hàng
                int soDonHangCho = donHangService.demDonHangChoXacNhan(nguoiDungId);
                model.addAttribute("soDonHangCho", soDonHangCho);
            }
        }

        // Lấy danh mục cha và con cho menu dropdown
        List<DanhMuc> danhMucChaList = danhMucRepository.findByDanhMucChaIdIsNull();
        model.addAttribute("danhMucChaList", danhMucChaList);

        // Tạo map danh mục con cho mỗi danh mục cha
        for (DanhMuc dmCha : danhMucChaList) {
            List<DanhMuc> danhMucConList = danhMucRepository.findByDanhMucChaId(dmCha.getId());
            model.addAttribute("danhMucCon_" + dmCha.getId(), danhMucConList);
        }
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

        // Lấy sản phẩm liên quan
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

    // Danh mục - Hiển thị theo danh mục cha hoặc con
    @GetMapping("/danh-muc/{id}")
    public String danhMuc(
            @PathVariable Integer id,
            @RequestParam(value = "loc", required = false) Integer locDanhMucConId,
            Model model) {

        DanhMuc danhMuc = danhMucRepository.findById(id).orElse(null);
        model.addAttribute("danhMuc", danhMuc);

        List<SanPham> sanPhamList = new ArrayList<>();
        List<DanhMuc> danhMucConList = new ArrayList<>();

        if (danhMuc != null) {
            // Kiểm tra xem đây có phải danh mục cha không
            if (danhMuc.getDanhMucChaId() == null) {
                // Đây là danh mục cha (Manga hoặc Light Novel)
                danhMucConList = danhMucRepository.findByDanhMucChaId(id);
                model.addAttribute("danhMucConList", danhMucConList);

                if (locDanhMucConId != null) {
                    // Lọc theo danh mục con cụ thể
                    sanPhamList = sanPhamRepository.findByDanhMucId(locDanhMucConId);
                    DanhMuc danhMucConDangLoc = danhMucRepository.findById(locDanhMucConId).orElse(null);
                    model.addAttribute("danhMucConDangLoc", danhMucConDangLoc);
                } else {
                    // Hiển thị tất cả sản phẩm của danh mục cha (tất cả danh mục con)
                    List<Integer> danhMucIds = danhMucConList.stream()
                            .map(DanhMuc::getId)
                            .collect(Collectors.toList());
                    if (!danhMucIds.isEmpty()) {
                        sanPhamList = sanPhamRepository.findByDanhMucIdIn(danhMucIds);
                    }
                }
            } else {
                // Đây là danh mục con - hiển thị sản phẩm của danh mục con này
                sanPhamList = sanPhamRepository.findByDanhMucId(id);

                // Lấy thông tin danh mục cha để hiển thị breadcrumb
                DanhMuc danhMucCha = danhMucRepository.findById(danhMuc.getDanhMucChaId()).orElse(null);
                model.addAttribute("danhMucCha", danhMucCha);
            }
        }

        model.addAttribute("sanPhamList", sanPhamList);
        model.addAttribute("locDanhMucConId", locDanhMucConId);

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

    // Tất cả sản phẩm với phân trang
    @GetMapping("/tat-ca-san-pham")
    public String tatCaSanPham(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 12);
        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(pageable);

        model.addAttribute("sanPhamPage", sanPhamPage);

        return "client/tat-ca-san-pham";
    }

    // Giỏ hàng
    @GetMapping("/gio-hang")
    public String gioHang(Model model, Authentication authentication) {
        Integer nguoiDungId = gioHangService.layNguoiDungId(authentication);
        if (nguoiDungId != null) {
            List<GioHangChiTiet> danhSachSanPham = gioHangService.layDanhSachSanPham(nguoiDungId);
            Double tongTien = gioHangService.tinhTongTien(nguoiDungId);

            model.addAttribute("danhSachSanPham", danhSachSanPham);
            model.addAttribute("tongTien", tongTien);
        }
        return "client/gio-hang";
    }

    // Thêm vào giỏ hàng
    @PostMapping("/gio-hang/them")
    @ResponseBody
    public Map<String, Object> themGioHang(
            @RequestParam Integer sanPhamId,
            @RequestParam(defaultValue = "1") Integer soLuong,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer nguoiDungId = gioHangService.layNguoiDungId(authentication);
            if (nguoiDungId != null) {
                gioHangService.themSanPham(nguoiDungId, sanPhamId, soLuong);

                // Đếm lại số lượng trong giỏ
                int soLuongGioHang = gioHangService.demSoLuongSanPham(nguoiDungId);

                response.put("success", true);
                response.put("message", "Đã thêm sản phẩm vào giỏ hàng!");
                response.put("soLuongGioHang", soLuongGioHang);
            } else {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập!");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return response;
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
            List<GioHangChiTiet> danhSachSanPham = gioHangService.layDanhSachSanPham(nguoiDungId);
            Double tongTien = gioHangService.tinhTongTien(nguoiDungId);

            model.addAttribute("danhSachSanPham", danhSachSanPham);
            model.addAttribute("tongTien", tongTien);
        }
        return "client/thanh-toan";
    }
    // Xử lý thanh toán
    @PostMapping("/thanh-toan/xu-ly")
    public String xuLyThanhToan(
            @RequestParam String hoTen,
            @RequestParam String sdt,
            @RequestParam String diaChi,
            @RequestParam String phuongThucThanhToan,
            @RequestParam(required = false) String ghiChu,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Integer nguoiDungId = gioHangService.layNguoiDungId(authentication);

            if (nguoiDungId == null) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập!");
                return "redirect:/login";
            }

            // Tạo đơn hàng
            DonHang donHang = donHangService.taoDonHang(
                    nguoiDungId,
                    diaChi,
                    sdt,
                    phuongThucThanhToan,
                    ghiChu
            );

            redirectAttributes.addFlashAttribute("message", "Đặt hàng thành công! Mã đơn hàng: " + donHang.getMaDonHang());
            return "redirect:/client/don-hang/" + donHang.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/client/gio-hang";
        }
    }

    // Xem chi tiết đơn hàng
    @GetMapping("/don-hang/{id}")
    public String chiTietDonHang(
            @PathVariable Integer id,
            Model model,
            Authentication authentication) {

        Integer nguoiDungId = gioHangService.layNguoiDungId(authentication);
        DonHang donHang = donHangService.layChiTietDonHang(id);

        // Kiểm tra quyền xem đơn hàng
        if (donHang == null || !donHang.getNguoiDungId().equals(nguoiDungId)) {
            return "redirect:/client/home";
        }

        List<DonHangChiTiet> chiTietList = donHangService.layDanhSachChiTiet(id);

        model.addAttribute("donHang", donHang);
        model.addAttribute("chiTietList", chiTietList);

        return "client/chi-tiet-don-hang";
    }

    // Danh sách đơn hàng
    @GetMapping("/don-hang")
    public String danhSachDonHang(Model model, Authentication authentication) {
        Integer nguoiDungId = gioHangService.layNguoiDungId(authentication);

        if (nguoiDungId != null) {
            List<DonHang> danhSachDonHang = donHangService.layDanhSachDonHang(nguoiDungId);
            model.addAttribute("danhSachDonHang", danhSachDonHang);
        }

        return "client/danh-sach-don-hang";
    }

    // Hủy đơn hàng
    @PostMapping("/don-hang/huy")
    public String huyDonHang(
            @RequestParam Integer donHangId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Integer nguoiDungId = gioHangService.layNguoiDungId(authentication);
        boolean success = donHangService.huyDonHang(donHangId, nguoiDungId);

        if (success) {
            redirectAttributes.addFlashAttribute("message", "Đã hủy đơn hàng thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy đơn hàng này!");
        }

        return "redirect:/client/don-hang";
    }
}