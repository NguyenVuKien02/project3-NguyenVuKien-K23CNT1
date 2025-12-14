package com.nvkproject3.controller.client;

import com.nvkproject3.model.SanPham;
import com.nvkproject3.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final SanPhamService sanPhamService;

    @GetMapping
    public String home(Model model) {
        List<SanPham> sanPhamMoi = sanPhamService.getAllSanPham();
        // Có thể thêm logic phân trang
        model.addAttribute("sanPhams", sanPhamMoi);
        return "client/home";
    }

    @GetMapping("/san-pham/{id}")
    public String chiTietSanPham(@PathVariable Long id, Model model) {
        SanPham sanPham = sanPhamService.getSanPhamById(id);
        model.addAttribute("sanPham", sanPham);
        return "client/sanpham/chitiet";
    }

    @GetMapping("/tim-kiem")
    public String timKiem(@RequestParam String keyword, Model model) {
        List<SanPham> ketQua = sanPhamService.searchSanPham(keyword);
        model.addAttribute("sanPhams", ketQua);
        model.addAttribute("keyword", keyword);
        return "client/sanpham/timkiem";
    }
}