package com.nvkproject3.service;

import com.nvkproject3.config.CustomUserDetails;
import com.nvkproject3.model.GioHang;
import com.nvkproject3.model.GioHangChiTiet;
import com.nvkproject3.repository.GioHangChiTietRepository;
import com.nvkproject3.repository.GioHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;

    // Lấy hoặc tạo giỏ hàng cho user
    public GioHang layHoacTaoGioHang(Integer nguoiDungId) {
        return gioHangRepository.findByNguoiDungId(nguoiDungId)
                .orElseGet(() -> {
                    GioHang gioHang = new GioHang();
                    gioHang.setNguoiDungId(nguoiDungId);
                    return gioHangRepository.save(gioHang);
                });
    }

    // Thêm sản phẩm vào giỏ
    @Transactional
    public void themSanPham(Integer nguoiDungId, Integer sanPhamId, Integer soLuong) {
        GioHang gioHang = layHoacTaoGioHang(nguoiDungId);

        // Kiểm tra sản phẩm đã có trong giỏ chưa
        gioHangChiTietRepository.findByGioHangIdAndSanPhamId(gioHang.getId(), sanPhamId)
                .ifPresentOrElse(
                        // Nếu có rồi thì cộng thêm số lượng
                        chiTiet -> {
                            chiTiet.setSoLuong(chiTiet.getSoLuong() + soLuong);
                            gioHangChiTietRepository.save(chiTiet);
                        },
                        // Nếu chưa có thì tạo mới
                        () -> {
                            GioHangChiTiet chiTiet = new GioHangChiTiet();
                            chiTiet.setGioHangId(gioHang.getId());
                            chiTiet.setSanPhamId(sanPhamId);
                            chiTiet.setSoLuong(soLuong);
                            gioHangChiTietRepository.save(chiTiet);
                        }
                );

        // Cập nhật thời gian
        gioHang.setNgayCapNhat(LocalDateTime.now());
        gioHangRepository.save(gioHang);
    }

    // Lấy danh sách sản phẩm trong giỏ
    public List<GioHangChiTiet> layDanhSachSanPham(Integer nguoiDungId) {
        GioHang gioHang = layHoacTaoGioHang(nguoiDungId);
        return gioHangChiTietRepository.findByGioHangId(gioHang.getId());
    }

    // Cập nhật số lượng
    @Transactional
    public void capNhatSoLuong(Integer chiTietId, Integer soLuong) {
        gioHangChiTietRepository.findById(chiTietId).ifPresent(chiTiet -> {
            if (soLuong > 0) {
                chiTiet.setSoLuong(soLuong);
                gioHangChiTietRepository.save(chiTiet);
            } else {
                gioHangChiTietRepository.delete(chiTiet);
            }
        });
    }

    // Xóa sản phẩm khỏi giỏ
    @Transactional
    public void xoaSanPham(Integer chiTietId) {
        gioHangChiTietRepository.deleteById(chiTietId);
    }

    // Xóa toàn bộ giỏ hàng
    @Transactional
    public void xoaToanBoGioHang(Integer nguoiDungId) {
        GioHang gioHang = layHoacTaoGioHang(nguoiDungId);
        gioHangChiTietRepository.deleteByGioHangId(gioHang.getId());
    }

    // Đếm số sản phẩm trong giỏ
    public int demSoLuongSanPham(Integer nguoiDungId) {
        return layDanhSachSanPham(nguoiDungId).stream()
                .mapToInt(GioHangChiTiet::getSoLuong)
                .sum();
    }

    // Tính tổng tiền giỏ hàng
    public Double tinhTongTien(Integer nguoiDungId) {
        return layDanhSachSanPham(nguoiDungId).stream()
                .mapToDouble(GioHangChiTiet::getTongTien)
                .sum();
    }

    // Lấy ID người dùng từ Authentication
    public Integer layNguoiDungId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getNguoiDung().getId();
        }
        return null;
    }
}