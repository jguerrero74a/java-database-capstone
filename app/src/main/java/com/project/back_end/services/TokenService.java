package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret:default_secret_key_at_least_32_characters_long}")
    private String secret;

    public TokenService(AdminRepository adminRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Long id, String role) {
        return Jwts.builder()
                .subject(String.valueOf(id)) // .setSubject() -> .subject()
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // .setIssuedAt() -> .issuedAt()
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // .setExpiration() -> .expiration()
                .signWith(getSigningKey()) // Ya no requiere SignatureAlgorithm.HS256 explícito
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser() 
                .verifyWith(getSigningKey()) // .setSigningKey() -> .verifyWith()
                .build() // El .build() es esencial ahora
                .parseSignedClaims(token) // .parseClaimsJws() -> .parseSignedClaims()
                .getPayload() // .getBody() -> .getPayload()
                .getSubject();
    }
    
    public Long extractId(String token) {
        return Long.parseLong(extractEmail(token));
    }

    public boolean validateToken(String token, String userType) {
        try {
            String identifier = extractEmail(token);
            switch (userType.toUpperCase()) {
                case "ADMIN": return adminRepository.findByUsername(identifier) != null;
                case "DOCTOR": return doctorRepository.findByEmail(identifier) != null;
                case "PATIENT": return patientRepository.findByEmail(identifier) != null;
                default: return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}