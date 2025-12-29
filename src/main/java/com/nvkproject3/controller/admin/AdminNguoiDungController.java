package com.nvkproject3.controller.admin;

import com.nvkproject3.model.NguoiDung;
import com.nvkproject3.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/nguoi-dung")
public class AdminNguoiDungController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Danh sách người dùng - THÊM FILTER VAI TRÒ
     */
    @GetMapping
    public String danhSach(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String vaiTro,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by("ngayDangKy").descending());
        Page<NguoiDung> nguoiDungPage;

        // Filter by vai trò
        if (vaiTro != null && !vaiTro.trim().isEmpty()) {
            NguoiDung.VaiTro role = NguoiDung.VaiTro.valueOf(vaiTro);
            nguoiDungPage = nguoiDungRepository.findAll(pageable)
                    .map(nd -> nd); // Temporary, cần implement custom query

            // Filter in memory (tạm thời)
            java.util.List<NguoiDung> filtered = nguoiDungRepository.findByVaiTro(role);
            nguoiDungPage = new org.springframework.data.domain.PageImpl<>(
                    filtered.stream()
                            .skip(page * 15)
                            .limit(15)
                            .collect(java.util.stream.Collectors.toList()),
                    pageable,
                    filtered.size()
            );

            model.addAttribute("vaiTro", vaiTro);
        } else {
            nguoiDungPage = nguoiDungRepository.findAll(pageable);
        }

        // Statistics
        model.addAttribute("nguoiDungPage", nguoiDungPage);
        model.addAttribute("totalUsers", nguoiDungRepository.count());
        model.addAttribute("totalAdmin", nguoiDungRepository.findByVaiTro(NguoiDung.VaiTro.admin).size());
        model.addAttribute("totalKhachHang", nguoiDungRepository.findByVaiTro(NguoiDung.VaiTro.khach_hang).size());
        model.addAttribute("totalActive", nguoiDungRepository.findByTrangThai(NguoiDung.TrangThai.active).size());
        model.addAttribute("totalInactive", nguoiDungRepository.findByTrangThai(NguoiDung.TrangThai.inactive).size());

        return "admin/nguoi-dung/danh-sach";
    }

    /**
     * Chi tiết người dùng
     */
    @GetMapping("/chi-tiet/{id}")
    public String chiTiet(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id).orElse(null);

        if (nguoiDung == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
            return "redirect:/admin/nguoi-dung";
        }

        model.addAttribute("nguoiDung", nguoiDung);
        return "admin/nguoi-dung/chi-tiet";
    }

    /**
     * Form thêm người dùng
     */
    @GetMapping("/them")
    public String formThem(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        return "admin/nguoi-dung/them";
    }

    /**
     * Xử lý thêm người dùng
     */
    @PostMapping("/them")
    public String them(
            @ModelAttribute NguoiDung nguoiDung,
            @RequestParam String matKhau,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
                return "redirect:/admin/nguoi-dung/them";
            }

            nguoiDung.setMatKhau(passwordEncoder.encode(matKhau));
            nguoiDungRepository.save(nguoiDung);
            redirectAttributes.addFlashAttribute("message", "Thêm người dùng thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/nguoi-dung/them";
        }

        return "redirect:/admin/nguoi-dung";
    }

    /**
     * Form sửa người dùng
     */
    @GetMapping("/sua/{id}")
    public String formSua(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id).orElse(null);

        if (nguoiDung == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
            return "redirect:/admin/nguoi-dung";
        }

        model.addAttribute("nguoiDung", nguoiDung);
        return "admin/nguoi-dung/sua";
    }

    /**
     * Xử lý sửa người dùng
     */
    @PostMapping("/sua/{id}")
    public String sua(
            @PathVariable Integer id,
            @ModelAttribute NguoiDung nguoiDungMoi,
            @RequestParam(required = false) String matKhauMoi,
            RedirectAttributes redirectAttributes
    ) {
        try {
            NguoiDung nguoiDung = nguoiDungRepository.findById(id).orElse(null);

            if (nguoiDung == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
                return "redirect:/admin/nguoi-dung";
            }

            nguoiDung.setHoTen(nguoiDungMoi.getHoTen());
            nguoiDung.setSdt(nguoiDungMoi.getSdt());
            nguoiDung.setDiaChi(nguoiDungMoi.getDiaChi());
            nguoiDung.setVaiTro(nguoiDungMoi.getVaiTro());
            nguoiDung.setTrangThai(nguoiDungMoi.getTrangThai());

            if (matKhauMoi != null && !matKhauMoi.trim().isEmpty()) {
                nguoiDung.setMatKhau(passwordEncoder.encode(matKhauMoi));
            }

            nguoiDungRepository.save(nguoiDung);
            redirectAttributes.addFlashAttribute("message", "Cập nhật thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/nguoi-dung";
    }

    /**
     * Xóa người dùng
     */
    @PostMapping("/xoa/{id}")
    public String xoa(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung nguoiDung = nguoiDungRepository.findById(id).orElse(null);

            if (nguoiDung == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
                return "redirect:/admin/nguoi-dung";
            }

            nguoiDungRepository.delete(nguoiDung);
            redirectAttributes.addFlashAttribute("message", "Xóa người dùng thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/nguoi-dung";
    }

    /**
     * Kích hoạt/Vô hiệu hóa người dùng
     */
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung nguoiDung = nguoiDungRepository.findById(id).orElse(null);

            if (nguoiDung == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
                return "redirect:/admin/nguoi-dung";
            }

            if (nguoiDung.getTrangThai() == NguoiDung.TrangThai.active) {
                nguoiDung.setTrangThai(NguoiDung.TrangThai.inactive);
            } else {
                nguoiDung.setTrangThai(NguoiDung.TrangThai.active);
            }

            nguoiDungRepository.save(nguoiDung);
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/nguoi-dung";
    }
}