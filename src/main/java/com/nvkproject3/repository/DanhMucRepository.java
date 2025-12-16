package com.nvkproject3.repository;

import com.nvkproject3.model.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {

    // Lấy tất cả danh mục cha (không có danh mục cha)
    List<DanhMuc> findByDanhMucChaIdIsNull();

    // Lấy danh mục con theo id danh mục cha
    List<DanhMuc> findByDanhMucChaId(Integer danhMucChaId);

    // Tìm theo tên danh mục
    List<DanhMuc> findByTenDanhMucContainingIgnoreCase(String ten);
}