package com.FP_Final.FP.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.FP_Final.FP.config.JwtUtil;
import com.FP_Final.FP.service.ActivityLogService;

import jakarta.servlet.http.HttpServletRequest;

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

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private HttpServletRequest request;

    private final JdbcTemplate jdbcTemplate;

    public AuthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping(
            value = "/login",
            consumes = "application/json",
            produces = "application/json"
    )
    public Map<String, String> login(
            @RequestBody(
                description = "Credenciales de acceso",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                        name = "Ejemplo login",
                        value = "{\"username\": \"ko2\", \"password\": \"1234\"}"
                    )
                )
            )
            @org.springframework.web.bind.annotation.RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        System.out.println("ENTR\u00d3 AL LOGIN");
        System.out.println(loginData);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String role = jdbcTemplate.queryForObject(
                    "SELECT permiso FROM users WHERE username = ?",
                    new Object[]{username},
                    String.class
            );

            String token = jwtUtil.generateToken(username, role);

            activityLogService.log(username, "LOGIN_SUCCESS", username, request.getRemoteAddr());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return response;

        } catch (AuthenticationException e) {
            activityLogService.log(username != null ? username : "unknown", "LOGIN_FAILED", username, request.getRemoteAddr());
            throw e;
        }
    }

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "Este es un endpoint protegido. Est\u00e1s autenticado.";
    }
}
