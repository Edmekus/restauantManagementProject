package com.cafe_backend.Dao;

import com.cafe_backend.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryInterface extends JpaRepository<Category, Long> {

    List<Category> getAllCategory();
}
