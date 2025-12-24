package com.nvkproject3.controller.admin;

import com.nvkproject3.model.DanhMuc;
import com.nvkproject3.model.SanPham;
import com.nvkproject3.repository.DanhMucRepository;
import com.nvkproject3.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/san-pham")
public class AdminSanPhamController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    /**
     * Danh sách sản phẩm với phân trang và tìm kiếm
     */
    @GetMapping
    @Transactional(readOnly = true)  // QUAN TRỌNG: Thêm @Transactional
    public String danhSach(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<SanPham> sanPhamPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            sanPhamPage = sanPhamRepository.findByTenContainingIgnoreCase(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            sanPhamPage = sanPhamRepository.findAll(pageable);
        }

        // FORCE LOAD danh mục để tránh LazyInitializationException
        for (SanPham sp : sanPhamPage.getContent()) {
            sp.getDanhMucs().size(); // Force initialize
        }

        model.addAttribute("sanPhamPage", sanPhamPage);

        return "admin/san-pham/danh-sach";
    }

    /**
     * Hiển thị form thêm sản phẩm
     */
    @GetMapping("/them")
    public String hienThiFormThem(Model model) {
        List<DanhMuc> danhMucList = danhMucRepository.findAll();

        model.addAttribute("danhMucList", danhMucList);
        model.addAttribute("sanPham", new SanPham());

        return "admin/san-pham/them";
    }

    /**
     * Xử lý thêm sản phẩm
     */
    @PostMapping("/them")
    @Transactional
    public String themSanPham(
            @RequestParam String ten,
            @RequestParam(required = false) String maSku,
            @RequestParam(required = false) String moTa,
            @RequestParam Double gia,
            @RequestParam Integer soLuongTon,
            @RequestParam(required = false) List<Integer> danhMucIds,
            @RequestParam(required = false) String anhDaiDien,
            @RequestParam(required = false) String nhaXuatBan,
            @RequestParam(required = false) Integer namXuatBan,
            @RequestParam(required = false) Integer soTrang,
            @RequestParam(required = false) Double trongLuong,
            @RequestParam(required = false) String ngonNgu,
            RedirectAttributes redirectAttributes) {

        try {
            SanPham sanPham = new SanPham();
            sanPham.setTen(ten);
            sanPham.setMaSku(maSku);
            sanPham.setMoTa(moTa);
            sanPham.setGia(gia);
            sanPham.setSoLuongTon(soLuongTon);
            sanPham.setAnhDaiDien(anhDaiDien);
            sanPham.setNhaXuatBan(nhaXuatBan);
            sanPham.setNamXuatBan(namXuatBan);
            sanPham.setSoTrang(soTrang);
            sanPham.setTrongLuong(trongLuong);
            sanPham.setNgonNgu(ngonNgu);

            // Thêm danh mục many-to-many
            if (danhMucIds != null && !danhMucIds.isEmpty()) {
                Set<DanhMuc> danhMucs = new HashSet<>();
                for (Integer danhMucId : danhMucIds) {
                    danhMucRepository.findById(danhMucId).ifPresent(danhMucs::add);
                }
                sanPham.setDanhMucs(danhMucs);
            }

            sanPhamRepository.save(sanPham);

            redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công!");
            return "redirect:/admin/san-pham";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/san-pham/them";
        }
    }

    /**
     * Hiển thị form sửa sản phẩm
     */
    @GetMapping("/sua/{id}")
    @Transactional(readOnly = true)
    public String hienThiFormSua(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        SanPham sanPham = sanPhamRepository.findById(id).orElse(null);

        if (sanPham == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
            return "redirect:/admin/san-pham";
        }

        // Force load danh mục
        sanPham.getDanhMucs().size();

        List<DanhMuc> danhMucList = danhMucRepository.findAll();

        model.addAttribute("sanPham", sanPham);
        model.addAttribute("danhMucList", danhMucList);

        return "admin/san-pham/sua";
    }

    /**
     * Xử lý sửa sản phẩm
     */
    @PostMapping("/sua/{id}")
    @Transactional
    public String suaSanPham(
            @PathVariable Integer id,
            @RequestParam String ten,
            @RequestParam(required = false) String maSku,
            @RequestParam(required = false) String moTa,
            @RequestParam Double gia,
            @RequestParam Integer soLuongTon,
            @RequestParam(required = false) List<Integer> danhMucIds,
            @RequestParam(required = false) String anhDaiDien,
            @RequestParam(required = false) String nhaXuatBan,
            @RequestParam(required = false) Integer namXuatBan,
            @RequestParam(required = false) Integer soTrang,
            @RequestParam(required = false) Double trongLuong,
            @RequestParam(required = false) String ngonNgu,
            RedirectAttributes redirectAttributes) {

        try {
            SanPham sanPham = sanPhamRepository.findById(id).orElse(null);

            if (sanPham == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
                return "redirect:/admin/san-pham";
            }

            sanPham.setTen(ten);
            sanPham.setMaSku(maSku);
            sanPham.setMoTa(moTa);
            sanPham.setGia(gia);
            sanPham.setSoLuongTon(soLuongTon);
            sanPham.setAnhDaiDien(anhDaiDien);
            sanPham.setNhaXuatBan(nhaXuatBan);
            sanPham.setNamXuatBan(namXuatBan);
            sanPham.setSoTrang(soTrang);
            sanPham.setTrongLuong(trongLuong);
            sanPham.setNgonNgu(ngonNgu);

            // Cập nhật danh mục many-to-many
            sanPham.getDanhMucs().clear();
            if (danhMucIds != null && !danhMucIds.isEmpty()) {
                for (Integer danhMucId : danhMucIds) {
                    danhMucRepository.findById(danhMucId).ifPresent(sanPham.getDanhMucs()::add);
                }
            }

            sanPhamRepository.save(sanPham);

            redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công!");
            return "redirect:/admin/san-pham";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/san-pham/sua/" + id;
        }
    }

    /**
     * Xóa sản phẩm
     */
    @PostMapping("/xoa/{id}")
    @Transactional
    public String xoaSanPham(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            SanPham sanPham = sanPhamRepository.findById(id).orElse(null);

            if (sanPham == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
                return "redirect:/admin/san-pham";
            }

            sanPhamRepository.deleteById(id);

            redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công!");
            return "redirect:/admin/san-pham";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa sản phẩm này! Có thể đã có đơn hàng liên quan.");
            return "redirect:/admin/san-pham";
        }
    }

    /**
     * Xem chi tiết sản phẩm
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String chiTiet(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        SanPham sanPham = sanPhamRepository.findById(id).orElse(null);

        if (sanPham == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
            return "redirect:/admin/san-pham";
        }

        // Force load danh mục
        sanPham.getDanhMucs().size();

        model.addAttribute("sanPham", sanPham);

        return "admin/san-pham/chi-tiet";
    }
}