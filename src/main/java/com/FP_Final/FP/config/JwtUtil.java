package com.FP_Final.FP.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

// Utilidad para generar, validar y leer tokens JWT
@Component
public class JwtUtil {

    // Clave secreta cargada desde application.properties (nunca hardcodeada en el código)
    @Value("${jwt.secret}")
    private String secretKey;

    // Genera un token JWT con el username, rol y expiración de 10 horas
    public String generateToken(String username, String rol) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // Valida que el token pertenezca al usuario correcto y no haya expirado
    public boolean validateToken(String token, String username) {
        String usernameExtraido = extractUsername(token);
        return usernameExtraido.equals(username) && !isTokenExpired(token);
    }

    // Extrae el username (campo "sub") del payload del token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Comprueba si la fecha de expiración del token ya ha pasado
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}
