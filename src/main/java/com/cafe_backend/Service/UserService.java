package com.cafe_backend.Service;

import com.cafe_backend.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {

    ResponseEntity<String> signUp (Map<String, String> requestMap);

    ResponseEntity<String> logIn (Map<String, String> requestMap);

    ResponseEntity<List<UserWrapper>> getAllUser();

    ResponseEntity<String> update(Map<String, String> requestMap);

    ResponseEntity<String> ckecktoken();

    ResponseEntity<String> changePassword (Map<String, String> requestMap);

    ResponseEntity<String> forgotPassword(Map<String, String> requesMap);

    ResponseEntity<String> resetPassword(String token, String newPassword);
}
