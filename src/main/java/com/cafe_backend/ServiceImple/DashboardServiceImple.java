package com.cafe_backend.ServiceImple;

import com.cafe_backend.Dao.BillInterface;
import com.cafe_backend.Dao.CategoryInterface;
import com.cafe_backend.Dao.ProductInterface;
import com.cafe_backend.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class DashboardServiceImple implements DashboardService {

    @Autowired
    CategoryInterface categoryInterface;

    @Autowired
    ProductInterface productInterface;

    @Autowired
    BillInterface billInterface;

    @Override
    public ResponseEntity<Map<String, Object>> getCounts() {
        Map<String,Object> map = new HashMap<>();

        map.put("category",categoryInterface.count());
        map.put("product",productInterface.count());
        map.put("bill",billInterface.count());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
