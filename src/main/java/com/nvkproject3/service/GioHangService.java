package com.nvkproject3.service;

import com.nvkproject3.model.*;
import com.nvkproject3.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final GioHangChiTietRepository gioHangChiTietRepository;
    private final SanPhamRepository sanPhamRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public GioHang getOrCreateGioHang(Long userId) {
        Optional<GioHang> gioHangOpt = gioHangRepository.findByNguoiDungId(userId);

        if (gioHangOpt.isPresent()) {
            return gioHangOpt.get();
        } else {
            NguoiDung nguoiDung = nguoiDungRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

            GioHang gioHang = new GioHang();
            gioHang.setNguoiDung(nguoiDung);
            return gioHangRepository.save(gioHang);
        }
    }

    @Transactional
    public void addToCart(Long userId, Long sanPhamId, Integer soLuong) {
        GioHang gioHang = getOrCreateGioHang(userId);
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        Optional<GioHangChiTiet> existingItem = gioHangChiTietRepository
                .findByGioHangIdAndSanPhamId(gioHang.getId(), sanPhamId);

        if (existingItem.isPresent()) {
            GioHangChiTiet item = existingItem.get();
            item.setSoLuong(item.getSoLuong() + soLuong);
            gioHangChiTietRepository.save(item);
        } else {
            GioHangChiTiet newItem = new GioHangChiTiet();
            newItem.setGioHang(gioHang);
            newItem.setSanPham(sanPham);
            newItem.setSoLuong(soLuong);
            gioHangChiTietRepository.save(newItem);
        }
    }

    @Transactional
    public void removeFromCart(Long userId, Long itemId) {
        GioHang gioHang = getOrCreateGioHang(userId);
        gioHangChiTietRepository.deleteByIdAndGioHangId(itemId, gioHang.getId());
    }

    public Double calculateTotal(Long userId) {
        GioHang gioHang = getOrCreateGioHang(userId);
        return gioHangChiTietRepository.calculateTotalByGioHangId(gioHang.getId());
    }
}