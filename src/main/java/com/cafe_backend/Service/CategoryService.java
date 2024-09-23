package com.cafe_backend.Service;

import com.cafe_backend.Models.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {


    ResponseEntity<String> addNewCategory(Map<String, String> requestMap);

    ResponseEntity<List<Category>> getAllCategories(String filterValue);

    ResponseEntity<String> updateCategory(Map<String, String> requestMap);
}
