package com.nvkproject3.controller.admin;

import com.nvkproject3.model.DanhMuc;
import com.nvkproject3.repository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/danh-muc")
public class AdminDanhMucController {

    @Autowired
    private DanhMucRepository danhMucRepository;

    /**
     * Danh sách danh mục
     */
    @GetMapping
    public String danhSach(Model model) {
        List<DanhMuc> danhMucList = danhMucRepository.findAll(Sort.by("id").ascending());

        // Tách danh mục cha và danh mục con
        List<DanhMuc> danhMucCha = danhMucList.stream()
                .filter(dm -> dm.getDanhMucChaId() == null)
                .collect(java.util.stream.Collectors.toList());

        // Statistics
        long totalCategories = danhMucRepository.count();
        long parentCategories = danhMucList.stream()
                .filter(dm -> dm.getDanhMucChaId() == null)
                .count();
        long childCategories = danhMucList.stream()
                .filter(dm -> dm.getDanhMucChaId() != null)
                .count();

        model.addAttribute("danhMucList", danhMucList);
        model.addAttribute("danhMucCha", danhMucCha);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategories", childCategories);

        return "admin/danh-muc/danh-sach";
    }

    /**
     * Form thêm danh mục
     */
    @GetMapping("/them")
    public String formThem(Model model) {
        // Lấy danh sách danh mục cha
        List<DanhMuc> danhMucCha = danhMucRepository.findAll().stream()
                .filter(dm -> dm.getDanhMucChaId() == null)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("danhMuc", new DanhMuc());
        model.addAttribute("danhMucCha", danhMucCha);
        return "admin/danh-muc/them";
    }

    /**
     * Xử lý thêm danh mục
     */
    @PostMapping("/them")
    public String them(
            @ModelAttribute DanhMuc danhMuc,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Kiểm tra tên trùng
            if (danhMucRepository.existsByTen(danhMuc.getTen())) {
                redirectAttributes.addFlashAttribute("error", "Tên danh mục đã tồn tại!");
                return "redirect:/admin/danh-muc/them";
            }

            danhMucRepository.save(danhMuc);
            redirectAttributes.addFlashAttribute("message", "Thêm danh mục thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/danh-muc/them";
        }

        return "redirect:/admin/danh-muc";
    }

    /**
     * Form sửa danh mục
     */
    @GetMapping("/sua/{id}")
    public String formSua(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        DanhMuc danhMuc = danhMucRepository.findById(id).orElse(null);

        if (danhMuc == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy danh mục!");
            return "redirect:/admin/danh-muc";
        }

        // Lấy danh sách danh mục cha (trừ chính nó và danh mục con của nó)
        List<DanhMuc> danhMucCha = danhMucRepository.findAll().stream()
                .filter(dm -> dm.getDanhMucChaId() == null && !dm.getId().equals(id))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("danhMuc", danhMuc);
        model.addAttribute("danhMucCha", danhMucCha);
        return "admin/danh-muc/sua";
    }

    /**
     * Xử lý sửa danh mục
     */
    @PostMapping("/sua/{id}")
    public String sua(
            @PathVariable Integer id,
            @ModelAttribute DanhMuc danhMucMoi,
            RedirectAttributes redirectAttributes
    ) {
        try {
            DanhMuc danhMuc = danhMucRepository.findById(id).orElse(null);

            if (danhMuc == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy danh mục!");
                return "redirect:/admin/danh-muc";
            }

            // Cập nhật thông tin
            danhMuc.setTen(danhMucMoi.getTen());
            danhMuc.setMoTa(danhMucMoi.getMoTa());
            danhMuc.setDanhMucChaId(danhMucMoi.getDanhMucChaId());

            danhMucRepository.save(danhMuc);
            redirectAttributes.addFlashAttribute("message", "Cập nhật danh mục thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/danh-muc";
    }

    /**
     * Xóa danh mục
     */
    @PostMapping("/xoa/{id}")
    public String xoa(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            DanhMuc danhMuc = danhMucRepository.findById(id).orElse(null);

            if (danhMuc == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy danh mục!");
                return "redirect:/admin/danh-muc";
            }

            // Kiểm tra có danh mục con không
            List<DanhMuc> danhMucCon = danhMucRepository.findAll().stream()
                    .filter(dm -> id.equals(dm.getDanhMucChaId()))
                    .collect(java.util.stream.Collectors.toList());

            if (!danhMucCon.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục có danh mục con!");
                return "redirect:/admin/danh-muc";
            }

            danhMucRepository.delete(danhMuc);
            redirectAttributes.addFlashAttribute("message", "Xóa danh mục thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/danh-muc";
    }
}