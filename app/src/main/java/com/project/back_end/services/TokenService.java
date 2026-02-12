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
                .subject(String.valueOf(id)) 
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    // MANTENEMOS ESTE NOMBRE para que AppointmentService y PatientService no fallen
    public String extractEmail(String token) {
        return Jwts.parser() 
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    
    // Alias por si alguna otra clase usa extractSubject
    public String extractSubject(String token) {
        return extractEmail(token);
    }

    public Long extractId(String token) {
        return Long.parseLong(extractEmail(token));
    }

    public boolean validateToken(String token, String userType) {
        try {
            String subject = extractEmail(token);
            // Validamos usando findById porque el subject contiene el ID numérico
            switch (userType.toUpperCase()) {
                case "ADMIN": 
                    return adminRepository.findById(Long.parseLong(subject)).isPresent();
                case "DOCTOR": 
                    return doctorRepository.findById(Long.parseLong(subject)).isPresent();
                case "PATIENT": 
                    return patientRepository.findById(Long.parseLong(subject)).isPresent();
                default: return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}