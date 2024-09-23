package com.cafe_backend.Dao;

import com.cafe_backend.Models.User;
import com.cafe_backend.wrapper.UserWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInterface extends JpaRepository<User, Long> {

    // Assurez-vous que la requête nommée "User.findByEmail" est définie dans votre fichier JPQL/HQL.
    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

    // Assurez-vous que vous avez bien défini cette méthode en fonction des besoins de votre application.
    List<UserWrapper> getAllUser();

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
    void updateStatus(@Param("status") String status, @Param("id") Long id);

    List<User> findAllByRole(String admin);

    User findByResetToken(String resetToken);

}
