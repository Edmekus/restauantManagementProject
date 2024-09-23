package com.cafe_backend.Service;

import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Objects;

public interface DashboardService {


    ResponseEntity<Map<String, Object>> getCounts();
}
