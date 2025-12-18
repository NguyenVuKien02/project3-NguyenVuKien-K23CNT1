package com.nvkproject3.repository;

import com.nvkproject3.model.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {

    // Tìm danh mục cha (không có parent)
    List<DanhMuc> findByDanhMucChaIdIsNull();

    // Tìm danh mục con theo parent ID
    List<DanhMuc> findByDanhMucChaId(Integer danhMucChaId);

    // Tìm kiếm theo tên - SỬA: ten KHÔNG phải tenDanhMuc
    List<DanhMuc> findByTenContainingIgnoreCase(String keyword);
}