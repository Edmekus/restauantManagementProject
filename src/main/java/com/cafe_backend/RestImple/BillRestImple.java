package com.cafe_backend.RestImple;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.cafe_backend.Constants.RestaurantContants;
import com.cafe_backend.Models.Bill;
import com.cafe_backend.Rest.BillRest;
import com.cafe_backend.Service.BillService;
import com.cafe_backend.Util.RestaurantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class BillRestImple implements BillRest {

    @Autowired
    BillService billService;


    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try {
            return billService.generateReport(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        try {
            return billService.getBills();

        }catch (Exception p){
            p.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        try {
            return billService.getPdf(requestMap);
        }catch (Exception y){
            y.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBill(Long id) {
        try {
            return billService.deleteBill(id);
        }catch (Exception g){
            g.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
