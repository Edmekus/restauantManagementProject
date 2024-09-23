package com.cafe_backend.Rest;


import com.cafe_backend.Models.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/category")
public interface CategoryRest {

    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewCategory (@RequestBody(required = true) Map<String, String> requestMap);

    @GetMapping(path = "/all")
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam(required = false) String filterValue);

    @PostMapping(path = "/update")
    public ResponseEntity<String> updateCategory (@RequestBody(required = true) Map<String, String> requestMap);


}
