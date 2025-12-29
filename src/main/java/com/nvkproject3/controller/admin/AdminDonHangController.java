package com.nvkproject3.controller.admin;

import com.nvkproject3.model.DonHang;
import com.nvkproject3.model.DonHang.TrangThai;
import com.nvkproject3.model.DonHangChiTiet;
import com.nvkproject3.repository.DonHangRepository;
import com.nvkproject3.repository.DonHangChiTietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/don-hang")
public class AdminDonHangController {

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private DonHangChiTietRepository donHangChiTietRepository;

    /**
     * Danh sách đơn hàng
     */
    @GetMapping
    public String danhSach(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by("ngayDat").descending());
        Page<DonHang> donHangPage;

        // Search with filters
        if (keyword != null && !keyword.trim().isEmpty()) {
            donHangPage = donHangRepository.search(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else if (trangThai != null && !trangThai.isEmpty()) {
            TrangThai status = TrangThai.valueOf(trangThai);
            donHangPage = donHangRepository.findByTrangThai(status, pageable);
            model.addAttribute("trangThai", trangThai);
        } else {
            donHangPage = donHangRepository.findAll(pageable);
        }

        // Statistics
        model.addAttribute("donHangPage", donHangPage);
        model.addAttribute("totalOrders", donHangRepository.count());
        model.addAttribute("choXacNhan", donHangRepository.countByTrangThai(TrangThai.cho_xac_nhan));
        model.addAttribute("dangXuLy", donHangRepository.countByTrangThai(TrangThai.dang_xu_ly));
        model.addAttribute("dangGiao", donHangRepository.countByTrangThai(TrangThai.dang_giao));
        model.addAttribute("daGiao", donHangRepository.countByTrangThai(TrangThai.da_giao));
        model.addAttribute("daHuy", donHangRepository.countByTrangThai(TrangThai.da_huy));

        return "admin/don-hang/danh-sach";
    }

    /**
     * Chi tiết đơn hàng
     */
    @GetMapping("/{id}")
    public String chiTiet(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        DonHang donHang = donHangRepository.findById(id).orElse(null);

        if (donHang == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
            return "redirect:/admin/don-hang";
        }

        // Load chi tiết đơn hàng
        List<DonHangChiTiet> chiTietList = donHangChiTietRepository.findByDonHangId(id);

        model.addAttribute("donHang", donHang);
        model.addAttribute("chiTietList", chiTietList);

        return "admin/don-hang/chi-tiet";
    }

    /**
     * Cập nhật trạng thái
     */
    @PostMapping("/{id}/cap-nhat-trang-thai")
    public String capNhatTrangThai(
            @PathVariable Integer id,
            @RequestParam TrangThai trangThai,
            RedirectAttributes redirectAttributes
    ) {
        try {
            DonHang donHang = donHangRepository.findById(id).orElse(null);

            if (donHang == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
                return "redirect:/admin/don-hang";
            }

            donHang.setTrangThai(trangThai);
            donHangRepository.save(donHang);

            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/don-hang/" + id;
    }

    /**
     * Hủy đơn hàng
     */
    @PostMapping("/{id}/huy")
    public String huyDonHang(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            DonHang donHang = donHangRepository.findById(id).orElse(null);

            if (donHang == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
                return "redirect:/admin/don-hang";
            }

            // Chỉ cho phép hủy nếu chưa giao
            if (donHang.getTrangThai() == TrangThai.da_giao) {
                redirectAttributes.addFlashAttribute("error", "Không thể hủy đơn hàng đã giao!");
                return "redirect:/admin/don-hang/" + id;
            }

            donHang.setTrangThai(TrangThai.da_huy);
            donHangRepository.save(donHang);

            redirectAttributes.addFlashAttribute("message", "Đã hủy đơn hàng!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/don-hang/" + id;
    }
}