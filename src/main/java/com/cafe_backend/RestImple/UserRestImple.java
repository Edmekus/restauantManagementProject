package com.cafe_backend.RestImple;

import com.cafe_backend.Constants.RestaurantContants;
import com.cafe_backend.Rest.userRest;
import com.cafe_backend.Service.UserService;
import com.cafe_backend.Util.RestaurantUtils;
import com.cafe_backend.wrapper.UserWrapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200 " , allowCredentials = "true")  // Autorise le CORS pour localhost:4200
@Transactional
public class UserRestImple implements userRest {

    private static final Logger logger = LoggerFactory.getLogger(UserRestImple.class);

    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try {
            return userService.signUp(requestMap);
        } catch (Exception ex) {
            logger.error("Une erreur s'est produite ", ex);
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> logIn(Map<String, String> requestMap) {
        try {
            return userService.logIn(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try{
            return userService.getAllUser();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            return userService.update(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> ckecktoken() {
        try{
            return userService.ckecktoken();

        }catch (Exception e){
            e.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requesMap) {
        try {
            return userService.changePassword(requesMap);

        }catch (Exception e){
            e.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requesMap) {
        try {
            return userService.forgotPassword(requesMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> resetPassword(String token, String newPassword) {
        try {
            return userService.resetPassword(token, newPassword);
        } catch (Exception e) {
            logger.error("Erreur lors de la réinitialisation du mot de passe", e);
            return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
