package com.cafe_backend.RestImple;

import com.cafe_backend.Constants.RestaurantContants;
import com.cafe_backend.Rest.ProductRest;
import com.cafe_backend.Service.ProductService;
import com.cafe_backend.Util.RestaurantUtils;
import com.cafe_backend.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ProductRestImple implements ProductRest {

    @Autowired
    ProductService productService;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requesMap) {
        try {
            return productService.addNewProduct(requesMap);

        }catch (Exception t){
            t.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        try {
            return productService.getAllProducts();
        }catch (Exception r){
            r.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            return productService.updateProduct(requestMap);
        }catch (Exception z){
            z.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Long id) {
        try {
            return productService.deleteProduct(id);
        }catch (Exception r){
            r.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requesMap) {
        try {
            return productService.updateStatus(requesMap);
        }catch (Exception q){
            q.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Long id) {
        try {
            return productService.getByCategory(id);
        }catch (Exception l){
            l.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Long id) {
        try {
            return productService.getProductById(id);
        }catch (Exception k){
            k.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
