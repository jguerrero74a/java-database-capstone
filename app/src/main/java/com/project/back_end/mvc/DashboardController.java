package com.project.back_end.mvc;

import com.project.back_end.services.Service; // Revertido a Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * DashboardController maneja el renderizado de vistas de Thymeleaf
 * tras validar los tokens de acceso para administradores y doctores.
 */
@Controller
public class DashboardController {

    // Volvemos a inyectar Service para mantener la consistencia con el resto del proyecto
    @Autowired
    private Service service;

    /**
     * Maneja el acceso al tablero del administrador.
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        // Usamos el método de validación de Service
        Map<String, Object> validationResult = service.validateTokenForDashboard(token, "admin");

        // Verificamos si el token es válido
        if (validationResult.containsKey("isValid") && (boolean) validationResult.get("isValid")) {
            return "admin/adminDashboard";
        }

        // Redirección si falla la validación
        return "redirect:http://localhost:8080";
    }

    /**
     * Maneja el acceso al tablero del doctor.
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        Map<String, Object> validationResult = service.validateTokenForDashboard(token, "doctor");

        if (validationResult.containsKey("isValid") && (boolean) validationResult.get("isValid")) {
            return "doctor/doctorDashboard";
        }

        return "redirect:http://localhost:8080";
    }
}