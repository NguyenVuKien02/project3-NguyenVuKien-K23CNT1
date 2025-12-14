package com.nvkproject3.controller.admin;

import com.nvkproject3.model.SanPham;
import com.nvkproject3.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/san-pham")
@RequiredArgsConstructor
public class AdminSanPhamController {

    private final SanPhamService sanPhamService;

    @GetMapping
    public String listSanPham(Model model) {
        model.addAttribute("sanPhams", sanPhamService.getAllSanPham());
        return "admin/sanpham/list";
    }

    @GetMapping("/them")
    public String showAddForm(Model model) {
        model.addAttribute("sanPham", new SanPham());
        return "admin/sanpham/form";
    }

    @GetMapping("/sua/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        SanPham sanPham = sanPhamService.getSanPhamById(id);
        model.addAttribute("sanPham", sanPham);
        return "admin/sanpham/form";
    }

    @PostMapping("/luu")
    public String saveSanPham(@ModelAttribute SanPham sanPham,
                              @RequestParam("anhDaiDienFile") MultipartFile anhDaiDien) {
        sanPhamService.saveSanPham(sanPham, anhDaiDien);
        return "redirect:/admin/san-pham";
    }

    @GetMapping("/xoa/{id}")
    public String deleteSanPham(@PathVariable Long id) {
        sanPhamService.deleteSanPham(id);
        return "redirect:/admin/san-pham";
    }
}