package com.nvkproject3.controller;

import com.nvkproject3.model.NguoiDung;
import com.nvkproject3.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class RegisterController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Hiển thị trang đăng ký
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    /**
     * Xử lý đăng ký tài khoản
     */
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String hoTen,
            @RequestParam String email,
            @RequestParam String sdt,
            @RequestParam String matKhau,
            @RequestParam String xacNhanMatKhau,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            // 1. Validate dữ liệu
            if (hoTen == null || hoTen.trim().length() < 2) {
                model.addAttribute("error", "Họ tên phải có ít nhất 2 ký tự!");
                return "register";
            }

            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                model.addAttribute("error", "Email không hợp lệ!");
                return "register";
            }

            if (!sdt.matches("^[0-9]{10}$")) {
                model.addAttribute("error", "Số điện thoại phải có 10 chữ số!");
                return "register";
            }

            if (matKhau == null || matKhau.length() < 6) {
                model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
                return "register";
            }

            if (!matKhau.equals(xacNhanMatKhau)) {
                model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
                return "register";
            }

            // 2. Kiểm tra email đã tồn tại
            Optional<NguoiDung> existingUser = nguoiDungRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                model.addAttribute("error", "Email đã được đăng ký!");
                return "register";
            }

            // 3. Tạo người dùng mới
            NguoiDung nguoiDung = new NguoiDung();
            nguoiDung.setHoTen(hoTen.trim());
            nguoiDung.setEmail(email.trim().toLowerCase());
            nguoiDung.setSdt(sdt);
            nguoiDung.setMatKhau(passwordEncoder.encode(matKhau)); // Mã hóa mật khẩu
            nguoiDung.setVaiTro(NguoiDung.VaiTro.khach_hang); // Mặc định là khách hàng
            nguoiDung.setNgayDangKy(LocalDateTime.now());
            nguoiDung.setTrangThai(NguoiDung.TrangThai.active);

            // 4. Lưu vào database
            nguoiDungRepository.save(nguoiDung);

            // 5. Redirect về trang login với thông báo thành công
            redirectAttributes.addFlashAttribute("message",
                    "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "register";
        }
    }
}