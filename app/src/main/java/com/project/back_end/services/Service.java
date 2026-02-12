package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public Service(TokenService tokenService, AdminRepository adminRepository, 
                   DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // --- MÉTODOS PARA ADMIN ---
    public ResponseEntity<Map<String, String>> validateAdmin(Admin admin) {
        Map<String, String> response = new HashMap<>();
        
        // LOG DE DEPURACIÓN
        System.out.println(">>> [LOG ADMIN] Intentando validar usuario: [" + admin.getUsername() + "]");
        System.out.println(">>> [LOG ADMIN] Password recibida: [" + admin.getPassword() + "]");

        if (admin.getUsername() == null || admin.getUsername().isEmpty()) {
            System.out.println(">>> [LOG ADMIN] ERROR: El username llegó NULL o vacío.");
            response.put("message", "Credenciales incompletas");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Admin found = adminRepository.findByUsername(admin.getUsername());
        
        if (found != null && found.getPassword().equals(admin.getPassword())) {
            System.out.println(">>> [LOG ADMIN] SUCCESS: Credenciales correctas.");
            String token = tokenService.createToken(found.getId(), "ADMIN");
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        
        System.out.println(">>> [LOG ADMIN] FAIL: Credenciales no coinciden con la BD.");
        response.put("message", "¡Credenciales inválidas!");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // --- MÉTODOS PARA PACIENTE ---
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        String email = login.getIdentifier(); 
        
        System.out.println(">>> [LOG PACIENTE] Login recibido para: [" + email + "]");

        if (email == null) {
            response.put("message", "Email obligatorio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Patient p = patientRepository.findByEmail(email);
        if (p != null && p.getPassword().equals(login.getPassword())) {
            String token = tokenService.createToken(p.getId(), "PATIENT");
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        
        response.put("message", "¡Credenciales inválidas!");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    public boolean validatePatient(Patient p) {
        if (p == null || p.getEmail() == null) return false;
        return patientRepository.findByEmail(p.getEmail()) == null;
    }

    public ResponseEntity<?> filterPatient(String condition, String name, String token) {
        return ResponseEntity.ok(new ArrayList<Appointment>());
    }

    // --- MÉTODOS PARA DOCTOR ---
    public Map<String, Object> filterDoctor(String name, String specialty, String city) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorRepository.findAll());
        return response;
    }

    // --- MÉTODOS PARA CITAS ---
    public ResponseEntity<Map<String, String>> validateAppointment(Appointment app) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "validated");
        return ResponseEntity.ok(response);
    }

    // --- UTILIDADES ---
    public boolean validateToken(String token, String role) {
        return tokenService.validateToken(token, role);
    }

    public Map<String, Object> validateTokenForDashboard(String token, String role) {
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", validateToken(token, role));
        return response;
    }
}