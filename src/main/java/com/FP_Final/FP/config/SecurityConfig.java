package com.FP_Final.FP.config;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Configuración central de Spring Security: CORS, JWT, sesiones y permisos por rol
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        http
                // CORS: solo los orígenes autorizados pueden llamar a la API
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "http://localhost:4200",
                            "https://farmacia-ko2.up.railway.app",
                            "https://pharma.ko2-oreilly.com",
                            "https://pharma-b.ko2-oreilly.com",
                            "https://farmacia-ko2-frontend.vercel.app",
                            "https://farmacia-ko2-back-production.up.railway.app"
                    ));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))

                // CSRF desactivado porque usamos JWT (sin cookies de sesión no hay riesgo CSRF)
                .csrf(csrf -> csrf.disable())

                // Sin sesiones en servidor — cada petición se autentica con su propio JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // OPTIONS siempre libre — los navegadores hacen preflight antes de cada petición CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // El login no requiere token (es donde se obtiene)
                        .requestMatchers("/auth/**").permitAll()

                        // Swagger accesible sin autenticar (solo documentación)
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        // El historial de actividad solo lo puede ver el SUPERADMIN
                        .requestMatchers("/activity-log/**").hasRole("SUPERADMIN")

                        // Todo lo demás requiere un JWT válido
                        .anyRequest().authenticated()
                )

                // Registramos nuestro filtro JWT antes del filtro estándar de Spring Security
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Expone el AuthenticationManager como bean para poder inyectarlo en AuthController
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // BCrypt para encriptar contraseñas — coste por defecto (10 rounds), suficientemente seguro
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Carga usuarios y roles desde la base de datos con las consultas SQL del proyecto
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        // Consulta para cargar el usuario por username
        manager.setUsersByUsernameQuery(
            "SELECT username, password, enabled FROM users WHERE username = ?"
        );
        // Consulta para cargar su rol — añadimos el prefijo ROLE_ que exige Spring Security
        manager.setAuthoritiesByUsernameQuery(
            "SELECT username, CONCAT('ROLE_', permiso) FROM users WHERE username = ?"
        );
        return manager;
    }
}
