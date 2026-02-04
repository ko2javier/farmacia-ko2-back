package com.FP_Final.FP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.FP_Final.FP.config.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final JdbcTemplate jdbcTemplate; // Declara JdbcTemplate

    public AuthController( JdbcTemplate jdbcTemplate) {
              
        this.jdbcTemplate = jdbcTemplate; // Inyecta JdbcTemplate
    }
    
    

    // Endpoint para login
    @PostMapping(
    	    value = "/login",
    	    consumes = "application/json",
    	    produces = "application/json"
    	)
    public Map<String, String> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        System.out.println("ENTRÓ AL LOGIN");
        System.out.println(loginData);

        // Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        
        // Obtener el rol desde la tabla users
        String role = jdbcTemplate.queryForObject(
            "SELECT permiso FROM users WHERE username = ?",
            new Object[]{username},
            String.class
        );

     // Generar el token JWT con el rol incluido
        String token = jwtUtil.generateToken(username, role);

        // Devolver el token como respuesta
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
    
    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "Este es un endpoint protegido. Estás autenticado.";
    }
}
