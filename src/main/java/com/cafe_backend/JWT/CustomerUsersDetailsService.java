package com.cafe_backend.JWT;

import com.cafe_backend.Dao.UserInterface;
import com.cafe_backend.Models.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerUsersDetailsService implements UserDetailsService {

    private final UserInterface userInterface;

    private User userDetail;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername with email: {}", userEmail);

        // Utilisation de Optional pour plus de clarté
        User userDetail = Optional.ofNullable(userInterface.findByEmail(userEmail))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // Set the userDetail field
        this.userDetail = userDetail;

        return new org.springframework.security.core.userdetails.User(
                userDetail.getEmail(),
                userDetail.getPassword(),
                new ArrayList<>() // Add authorities if available
        );
    }

    public User getUserDetail() {
        return userDetail;
    }
}
