package com.prestamo.dalp.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "mi_secreto_12345678901234567890123456789012"; // 🔹 Debe ser de al menos 32 caracteres (256 bits)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 🔹 1 hora

    // 🔹 Método para generar la clave de firma
    private SecretKey getSignKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 🔹 Generar un token con rol
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("role", role) // 🔹 Agregar el rol al token
                .signWith(getSignKey())
                .compact();
    }

    // 🔹 Extraer el nombre de usuario desde el token
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 🔹 Extraer el rol desde el token
    public String extractRole(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class); // 🔹 Extrae el rol del payload
    }

    // 🔹 Verificar si el token ha expirado
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 🔹 Extraer la fecha de expiración
    public Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    // 🔹 Validar el token comparando el usuario y la expiración
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
