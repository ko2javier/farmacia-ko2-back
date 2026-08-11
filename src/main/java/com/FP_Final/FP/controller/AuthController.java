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

    // POST /auth/login — autentica al usuario y devuelve un token JWT si las credenciales son correctas
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
            @org.springframework.web.bind.annotation.RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("username");
        String password = credenciales.get("password");

        try {
            // Spring Security verifica username y password contra la base de datos
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Recuperamos el rol del usuario para incluirlo en el token
            String rol = jdbcTemplate.queryForObject(
                    "SELECT permiso FROM users WHERE username = ?",
                    new Object[]{username},
                    String.class
            );

            String token = jwtUtil.generateToken(username, rol);

            activityLogService.log(username, "LOGIN_SUCCESS", username, request.getRemoteAddr());

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("token", token);
            return respuesta;

        } catch (AuthenticationException e) {
            // Credenciales incorrectas — registramos el intento fallido
            activityLogService.log(username != null ? username : "unknown", "LOGIN_FAILED", username, request.getRemoteAddr());
            throw e;
        }
    }

    // GET /auth/protected — endpoint de prueba para verificar que el token es válido
    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "Este es un endpoint protegido. Estás autenticado.";
    }
}
