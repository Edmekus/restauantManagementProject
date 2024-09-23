package com.cafe_backend.RestImple;

import com.cafe_backend.Rest.DashboardRet;
import com.cafe_backend.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
public class DashboardRestImple implements DashboardRet {

    @Autowired
    DashboardService dashboardService;


    @Override
    public ResponseEntity<Map<String, Object>> getCounts() {
        return dashboardService.getCounts();
    }
}
