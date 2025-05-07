package com.itj.blockcert.Config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.itj.blockcert.Repository.UserRepository;

@Configuration
public class SecurityConfig {
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll() // Allow "/auth/**" &  "/certificates/**"
                .requestMatchers("/certificates/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login.disable()) // Disable default login form
        	.logout(logout -> logout
                .logoutUrl("/auth/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((req, res, auth) -> {
                	res.setStatus(200);
                	res.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                    res.setHeader("Access-Control-Allow-Credentials", "true");
                    res.getWriter().write("Logged out successfully");
                })
            )
            .sessionManagement(session -> session
                    .maximumSessions(10) // optional: restrict concurrent logins
                );

        return http.build();
    }
	
	// Register CORS configuration for Security Filter Chain
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true); // required for session cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	
//	public UserDetailsService userDetailsService(UserRepository repo) {
//	    return new CustomUserDetailsService(repo);
//	}
}
