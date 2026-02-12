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

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        
        // REGLA DE ORO: Si la guía pide 'email', pero el DTO usa 'identifier',
        // extraemos el valor que venga del login. 
        // Si el DTO no mapea 'email' automáticamente, este valor podría ser null.
        String email = login.getIdentifier(); 

        if (email == null) {
            response.put("message", "Error: El campo 'email' es obligatorio según la guía.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Patient p = patientRepository.findByEmail(email);
        
        if (p != null && p.getPassword().equals(login.getPassword())) {
            String token = tokenService.createToken(p.getId(), "PATIENT");
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        
        response.put("message", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // ... (El resto de métodos se mantienen igual)
    
    public boolean validateToken(String token, String role) {
        return tokenService.validateToken(token, role);
    }

    public Map<String, Object> validateTokenForDashboard(String token, String role) {
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", validateToken(token, role));
        return response;
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin admin) {
        Map<String, String> response = new HashMap<>();
        Admin found = adminRepository.findByUsername(admin.getUsername());
        if (found != null && found.getPassword().equals(admin.getPassword())) {
            response.put("token", tokenService.createToken(found.getId(), "ADMIN"));
            return ResponseEntity.ok(response);
        }
        response.put("message", "Invalid admin credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String city) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findAll(); 
        response.put("doctors", doctors);
        return response;
    }

    public boolean validatePatient(Patient p) {
        if (p == null || p.getEmail() == null) return false;
        return patientRepository.findByEmail(p.getEmail()) == null;
    }

    public ResponseEntity<Map<String, String>> validateAppointment(Appointment app) {
        Map<String, String> response = new HashMap<>();
        if (app == null) {
            response.put("message", "Invalid appointment data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("status", "validated");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> filterPatient(String condition, String name, String token) {
        List<Appointment> appointments = new ArrayList<>();
        return ResponseEntity.ok(appointments);
    }
}