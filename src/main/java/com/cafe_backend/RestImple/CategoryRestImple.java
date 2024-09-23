package com.cafe_backend.RestImple;

import com.cafe_backend.Constants.RestaurantContants;
import com.cafe_backend.Models.Category;
import com.cafe_backend.Rest.CategoryRest;
import com.cafe_backend.Service.CategoryService;
import com.cafe_backend.Util.RestaurantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryRestImple implements CategoryRest {

    @Autowired
    private CategoryService categoryService;

    @Override
    public ResponseEntity<String> addNewCategory(@RequestBody Map<String, String> requestMap) {
        try {
            ResponseEntity<String> response = categoryService.addNewCategory(requestMap);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return RestaurantUtils.getResponseEntity("Access denied non autorisé", HttpStatus.UNAUTHORIZED);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return RestaurantUtils.getResponseEntity("Une erreur s'est produite lors de l'ajout. ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategories(String filterValue) {
        try {
            return categoryService.getAllCategories(filterValue);

        }catch (Exception g){
            g.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            return categoryService.updateCategory(requestMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
