package com.cafe_backend.ServiceImple;

import com.cafe_backend.Constants.RestaurantContants;
import com.cafe_backend.Dao.ProductInterface;
import com.cafe_backend.JWT.JwtFilter;
import com.cafe_backend.Models.Category;
import com.cafe_backend.Models.Product;
import com.cafe_backend.Service.ProductService;
import com.cafe_backend.Util.RestaurantUtils;
import com.cafe_backend.wrapper.ProductWrapper;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PrductServiceImple implements ProductService {


    @Autowired
    ProductInterface productInterface;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap,false)){
                    productInterface.save(getProductFromMap(requestMap,false));
                    return RestaurantUtils.getResponseEntity("Le produit a été ajouter avec succès",HttpStatus.OK);
                }
                return RestaurantUtils.getResponseEntity(RestaurantContants.invalide_data,HttpStatus.BAD_REQUEST);

            }else {
                return RestaurantUtils.getResponseEntity(RestaurantContants.unauthorized_access,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception p){
            p.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateId){
                return true;
            } else if (!validateId) {
                return true;
            }
        }
        return false;
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Long.parseLong(requestMap.get("categoryId")));

        Product product = new Product();
        if (isAdd){
            product.setId(Long.parseLong(requestMap.get("id")));
        }else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        try {
            return new ResponseEntity<>(productInterface.getAllProducts(),HttpStatus.OK);
        }catch (Exception s){
            s.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap, true)){
                    Optional<Product> optional =productInterface.findById(Long.parseLong(requestMap.get("id")));
                    if (!optional.isEmpty()){
                        Product product = getProductFromMap(requestMap,true);
                        product.setStatus(optional.get().getStatus());
                        productInterface.save(product);
                        return RestaurantUtils.getResponseEntity("Le produit a été mise à jour avec succès",HttpStatus.OK);
                    }else {
                        return RestaurantUtils.getResponseEntity("L'identifiant du produit n'existe pas",HttpStatus.OK);
                    }
                }else {
                    return RestaurantUtils.getResponseEntity(RestaurantContants.invalide_data,HttpStatus.BAD_REQUEST);
                }
            }else {
                return RestaurantUtils.getResponseEntity(RestaurantContants.unauthorized_access,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception a){
            a.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Long id) {
        try {
            if (jwtFilter.isAdmin()){
                Optional optional = productInterface.findById(id);
                if (!optional.isEmpty()){
                    productInterface.deleteById(id);
                    return RestaurantUtils.getResponseEntity("Le produit a été supprimé avec succès",HttpStatus.OK);
                }else {
                    return RestaurantUtils.getResponseEntity("L'identifiant du produit n'existe pas",HttpStatus.OK);
                }
            }else {
                return RestaurantUtils.getResponseEntity(RestaurantContants.unauthorized_access,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception x){
            x.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requesMap) {
        try {
            if (jwtFilter.isAdmin()){
                Optional optional = productInterface.findById(Long.parseLong(requesMap.get("id")));
                if (!optional.isEmpty()){
                    productInterface.updateProductStatus(requesMap.get("status"),Long.parseLong(requesMap.get("id")));
                    return RestaurantUtils.getResponseEntity("Le status du produit a été mise à jour avec succès",HttpStatus.OK);
                }else {
                    return RestaurantUtils.getResponseEntity("L'identifiant du produit n'existe pas",HttpStatus.OK);
                }
            }else {
                return RestaurantUtils.getResponseEntity(RestaurantContants.unauthorized_access,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception h){
            h.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Long id) {
        try {
            return new ResponseEntity<>(productInterface.getProductByCategory(id),HttpStatus.OK);
        }catch (Exception n){
            n.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Long id) {
        try {
            return new ResponseEntity<>(productInterface.getProductById(id),HttpStatus.OK);
        }catch (Exception w){
            w.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
