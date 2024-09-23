package com.cafe_backend.Rest;


import com.cafe_backend.Models.Category;
import com.cafe_backend.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductRest {

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewProduct (@RequestBody Map<String,String> requesMap);

    @GetMapping(path = "/all")
    public ResponseEntity<List<ProductWrapper>> getAllProducts();

    @PostMapping(path = "/update")
    public ResponseEntity<String> updateProduct (@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id);

    @PostMapping(path = "/updateStatus")
    ResponseEntity<String> updateStatus (@RequestBody Map<String,String> requesMap);

    @GetMapping(path = "/getByCategory/{id}")
    public ResponseEntity<List<ProductWrapper>> getByCategory(@PathVariable Long id);

    @GetMapping(path = "/getById/{id}")
    public ResponseEntity<ProductWrapper> getProductById(@PathVariable Long id);







}
