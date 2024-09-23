package com.cafe_backend.Dao;

import com.cafe_backend.Models.Product;
import com.cafe_backend.wrapper.ProductWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductInterface extends JpaRepository<Product, Long> {

    List<ProductWrapper> getProductByCategory(@Param("id") Long id);

    List<ProductWrapper> getAllProducts();

    @Modifying
    @Transactional
    Integer updateProductStatus(@Param("status") String status,@Param("id") Long id);

    ProductWrapper getProductById(@Param("id") Long id);
}
