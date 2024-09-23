package com.cafe_backend.Service;

import com.cafe_backend.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {


    ResponseEntity<String> addNewProduct(Map<String ,String> requestMap);

    ResponseEntity<List<ProductWrapper>> getAllProducts();

    ResponseEntity<String> updateProduct(Map<String, String> requestMap);

    ResponseEntity<String> deleteProduct(Long id);

    ResponseEntity<String> updateStatus(Map<String, String> requesMap);

    ResponseEntity<List<ProductWrapper>> getByCategory(Long id);

    ResponseEntity<ProductWrapper> getProductById(Long id);
}
