package com.cafe_backend.ServiceImple;

import com.cafe_backend.Constants.RestaurantContants;
import com.cafe_backend.Dao.CategoryInterface;
import com.cafe_backend.JWT.JwtFilter;
import com.cafe_backend.Models.Category;
import com.cafe_backend.Service.CategoryService;
import com.cafe_backend.Util.RestaurantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    @Override
    public List<Category> getAllCategories() {
        return categoryInterface.findAll();  // Utilisation de findAll
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
}
