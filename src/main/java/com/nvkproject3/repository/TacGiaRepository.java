package com.nvkproject3.repository;

import com.nvkproject3.model.TacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacGiaRepository extends JpaRepository<TacGia, Integer> {

    // Tìm tác giả theo tên (tìm gần đúng)
    List<TacGia> findByTenTacGiaContainingIgnoreCase(String ten);

    // Tìm theo quốc gia
    List<TacGia> findByQuocGia(String quocGia);
}