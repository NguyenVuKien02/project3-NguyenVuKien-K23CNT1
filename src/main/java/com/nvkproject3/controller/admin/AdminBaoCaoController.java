package com.nvkproject3.controller.admin;

import com.nvkproject3.model.DonHang;
import com.nvkproject3.repository.DanhMucRepository;
import com.nvkproject3.repository.DonHangRepository;
import com.nvkproject3.repository.NguoiDungRepository;
import com.nvkproject3.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/bao-cao")
public class AdminBaoCaoController {

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @GetMapping
    public String baoCao(
            @RequestParam(required = false) String timeRange,
            Model model
    ) {
        // Mặc định: 30 ngày gần đây
        if (timeRange == null || timeRange.isEmpty()) {
            timeRange = "30days";
        }

        LocalDateTime startDate = getStartDate(timeRange);
        List<DonHang> allOrders = donHangRepository.findAll();

        // Filter orders by date range
        List<DonHang> filteredOrders = allOrders.stream()
                .filter(dh -> dh.getNgayDat().isAfter(startDate))
                .collect(Collectors.toList());

        // === TỔNG QUAN ===
        long totalOrders = filteredOrders.size();
        long totalProducts = sanPhamRepository.count();
        long totalUsers = nguoiDungRepository.count();
        long totalCategories = danhMucRepository.count();

        // Tổng doanh thu
        double totalRevenue = filteredOrders.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.da_giao)
                .mapToDouble(DonHang::getTongTien)
                .sum();

        // Đơn hàng theo trạng thái (trong khoảng thời gian đã chọn)
        long ordersPending = filteredOrders.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.cho_xac_nhan)
                .count();
        long ordersProcessing = filteredOrders.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.dang_xu_ly)
                .count();
        long ordersShipping = filteredOrders.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.dang_giao)
                .count();
        long ordersCompleted = filteredOrders.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.da_giao)
                .count();
        long ordersCancelled = filteredOrders.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.da_huy)
                .count();

        // === BIỂU ĐỒ DOANH THU THEO THỜI GIAN ===
        Map<String, Double> revenueByDay = getRevenueByPeriod(allOrders, timeRange);

        // === BIỂU ĐỒ TRẠNG THÁI ĐƠN HÀNG ===
        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        ordersByStatus.put("Chờ xác nhận", ordersPending);
        ordersByStatus.put("Đang xử lý", ordersProcessing);
        ordersByStatus.put("Đang giao", ordersShipping);
        ordersByStatus.put("Đã giao", ordersCompleted);
        ordersByStatus.put("Đã hủy", ordersCancelled);

        // Add data to model
        model.addAttribute("timeRange", timeRange);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalRevenue", totalRevenue);

        model.addAttribute("ordersPending", ordersPending);
        model.addAttribute("ordersProcessing", ordersProcessing);
        model.addAttribute("ordersShipping", ordersShipping);
        model.addAttribute("ordersCompleted", ordersCompleted);
        model.addAttribute("ordersCancelled", ordersCancelled);

        model.addAttribute("revenueByDay", revenueByDay);
        model.addAttribute("ordersByStatus", ordersByStatus);

        return "admin/bao-cao/dashboard";
    }

    /**
     * Lấy doanh thu theo khoảng thời gian
     */
    private Map<String, Double> getRevenueByPeriod(List<DonHang> allOrders, String timeRange) {
        Map<String, Double> revenue = new LinkedHashMap<>();

        switch (timeRange) {
            case "7days":
                // 7 ngày - group by ngày
                for (int i = 6; i >= 0; i--) {
                    LocalDateTime date = LocalDateTime.now().minusDays(i);
                    String label = date.format(DateTimeFormatter.ofPattern("dd/MM"));
                    double dayRevenue = getRevenueForDate(allOrders, date);
                    revenue.put(label, dayRevenue);
                }
                break;

            case "30days":
                // 30 ngày - group by 5 ngày (6 điểm)
                for (int i = 25; i >= 0; i -= 5) {
                    LocalDateTime date = LocalDateTime.now().minusDays(i);
                    String label = date.format(DateTimeFormatter.ofPattern("dd/MM"));

                    // Tính tổng doanh thu 5 ngày
                    double periodRevenue = 0;
                    for (int j = 0; j < 5 && (i - j) >= 0; j++) {
                        periodRevenue += getRevenueForDate(allOrders, LocalDateTime.now().minusDays(i - j));
                    }
                    revenue.put(label, periodRevenue);
                }
                break;

            case "90days":
                // 90 ngày - group by 15 ngày (6 điểm)
                for (int i = 75; i >= 0; i -= 15) {
                    LocalDateTime date = LocalDateTime.now().minusDays(i);
                    String label = date.format(DateTimeFormatter.ofPattern("dd/MM"));

                    // Tính tổng doanh thu 15 ngày
                    double periodRevenue = 0;
                    for (int j = 0; j < 15 && (i - j) >= 0; j++) {
                        periodRevenue += getRevenueForDate(allOrders, LocalDateTime.now().minusDays(i - j));
                    }
                    revenue.put(label, periodRevenue);
                }
                break;

            case "1year":
                // 1 năm - group by tháng (12 điểm)
                for (int i = 11; i >= 0; i--) {
                    LocalDateTime date = LocalDateTime.now().minusMonths(i);
                    String label = date.format(DateTimeFormatter.ofPattern("MM/yyyy"));
                    double monthRevenue = getRevenueForMonth(allOrders, date);
                    revenue.put(label, monthRevenue);
                }
                break;

            default:
                // Mặc định: 7 ngày
                for (int i = 6; i >= 0; i--) {
                    LocalDateTime date = LocalDateTime.now().minusDays(i);
                    String label = date.format(DateTimeFormatter.ofPattern("dd/MM"));
                    double dayRevenue = getRevenueForDate(allOrders, date);
                    revenue.put(label, dayRevenue);
                }
        }

        return revenue;
    }

    /**
     * Lấy doanh thu cho 1 ngày cụ thể
     */
    private double getRevenueForDate(List<DonHang> orders, LocalDateTime date) {
        return orders.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.da_giao)
                .filter(dh -> dh.getNgayDat().toLocalDate().equals(date.toLocalDate()))
                .mapToDouble(DonHang::getTongTien)
                .sum();
    }

    /**
     * Lấy doanh thu cho 1 tháng cụ thể
     */
    private double getRevenueForMonth(List<DonHang> orders, LocalDateTime date) {
        return orders.stream()
                .filter(dh -> dh.getTrangThai() == DonHang.TrangThai.da_giao)
                .filter(dh -> dh.getNgayDat().getYear() == date.getYear()
                        && dh.getNgayDat().getMonth() == date.getMonth())
                .mapToDouble(DonHang::getTongTien)
                .sum();
    }

    /**
     * Lấy start date theo timeRange
     */
    private LocalDateTime getStartDate(String timeRange) {
        switch (timeRange) {
            case "7days":
                return LocalDateTime.now().minusDays(7);
            case "30days":
                return LocalDateTime.now().minusDays(30);
            case "90days":
                return LocalDateTime.now().minusDays(90);
            case "1year":
                return LocalDateTime.now().minusYears(1);
            default:
                return LocalDateTime.now().minusDays(30);
        }
    }
}