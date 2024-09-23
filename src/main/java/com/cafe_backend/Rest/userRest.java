package com.cafe_backend.Rest;

import com.cafe_backend.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "/user")
public interface userRest {


    @PostMapping(path = "/signUp")
    public ResponseEntity<String> signUp (@RequestBody(required = true)Map<String, String> requestMap);

    @PostMapping(path = "/logIn")
    public ResponseEntity<String> logIn (@RequestBody(required = true)Map<String, String> requestMap);

    @GetMapping(path = "/get")
    public ResponseEntity<List<UserWrapper>> getAllUsers ();

    @PostMapping(path = "/update")
    public ResponseEntity<String> update(@RequestBody(required = true)Map<String,String> requestMap);

    @GetMapping(path = "/ckeckToken")
    public ResponseEntity<String> ckecktoken ();

    @PostMapping(path = "/changePassword")
    ResponseEntity<String> changePassword (@RequestBody Map<String , String> requesMap);

    @PostMapping(path = "/forgotPassword")
    ResponseEntity<String> forgotPassword (@RequestBody Map<String , String> requesMap);

    @PostMapping(path = "/reset-password")
    ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword);



}
