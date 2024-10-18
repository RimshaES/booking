package com.rimsha.service;

import com.rimsha.model.db.entity.User;
import com.rimsha.model.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Поиск пользователя по email в базе данных
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() ->
                new UsernameNotFoundException("User not exists by Email"));

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getIsAdmin() ? "ADMIN" : "USER");

        // Возвращаем объект UserDetails без ролей (с пустым списком прав доступа)
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),     // Email
                user.getPassword(),  // Зашифрованный пароль
                List.of(authority)   // Пустой список прав доступа (GrantedAuthority)
        );
    }
}
