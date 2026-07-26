package com.example.valet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults()).authorizeHttpRequests(a -> a.requestMatchers("/public/**", "/h2-console/**").permitAll().anyRequest().authenticated()).httpBasic(Customizer.withDefaults()).headers(h -> h.frameOptions(f -> f.sameOrigin())).build();
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService users(PasswordEncoder e) {
        UserDetails admin = User.withUsername("admin").password(e.encode("admin123")).roles("ADMIN").build();
        UserDetails valet = User.withUsername("valet").password(e.encode("valet123")).roles("VALET").build();
        return new InMemoryUserDetailsManager(admin, valet);
    }
}