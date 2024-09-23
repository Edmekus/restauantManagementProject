package com.cafe_backend.Dao;

import com.cafe_backend.Models.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillInterface extends JpaRepository<Bill,Long> {
    
    List<Bill> getAllBills();


    List<Bill> getBillByUserName(@Param("username") String username);

}
