package com.cafe_backend.ServiceImple;

import com.cafe_backend.Constants.RestaurantContants;
import com.cafe_backend.Dao.CategoryInterface;
import com.cafe_backend.JWT.JwtFilter;
import com.cafe_backend.Models.Category;
import com.cafe_backend.Service.CategoryService;
import com.cafe_backend.Util.RestaurantUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImple implements CategoryService {

    @Autowired
    private CategoryInterface categoryInterface;

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            // Vérification du rôle de l'utilisateur
            if (!jwtFilter.isAdmin()) {
                return RestaurantUtils.getResponseEntity(RestaurantContants.unauthorized_access, HttpStatus.UNAUTHORIZED);
            }

            // Validation des données de la catégorie
            if (!validateCategoryMap(requestMap, false)) {
                return RestaurantUtils.getResponseEntity("Données de catégorie invalides", HttpStatus.BAD_REQUEST);
            }

            // Sauvegarde de la nouvelle catégorie
            categoryInterface.save(getCategoryFromMap(requestMap, false));
            return RestaurantUtils.getResponseEntity("Catégorie ajoutée avec succès", HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        // Validation du champ "name"
        if (!requestMap.containsKey("name") || requestMap.get("name").isEmpty()) {
            return false;
        }

        // Validation du champ "id" si nécessaire
        if (validateId && (!requestMap.containsKey("id") || requestMap.get("id").isEmpty())) {
            return false;
        }

        return true;
    }

    private Category getCategoryFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();

        // Ajout de l'ID si nécessaire
        if (isAdd && requestMap.containsKey("id")) {
            try {
                category.setId(Long.parseLong(requestMap.get("id")));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ID de catégorie non valide : " + requestMap.get("id"));
            }
        }

        category.setName(requestMap.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategories(String filterValue) {
        try {
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                log.info("inside if");
                return new ResponseEntity<List<Category>>(categoryInterface.getAllCategory(),HttpStatus.OK);
            }
            return new  ResponseEntity<>(categoryInterface.findAll(),HttpStatus.OK);
        }catch (Exception p){
            p.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateCategoryMap(requestMap, true)){
                    Optional optional = categoryInterface.findById(Long.parseLong(requestMap.get("id")));
                    if (!optional.isEmpty()){
                        categoryInterface.save(getCategoryFromMap(requestMap,true));
                        return RestaurantUtils.getResponseEntity("Catégorie mise à jour avec succès",HttpStatus.OK);

                    }else {
                        return RestaurantUtils.getResponseEntity("L' Id de la catégorie n'existe pas ",HttpStatus.OK);
                    }
                }
                return RestaurantUtils.getResponseEntity(RestaurantContants.invalide_data, HttpStatus.BAD_REQUEST);

            }else {
                return RestaurantUtils.getResponseEntity(RestaurantContants.unauthorized_access, HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception j){
            j.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
