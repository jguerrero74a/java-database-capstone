package com.project.back_end.mvc;

import com.project.back_end.services.TokenService; // Asegúrate de que el paquete de TokenService sea el correcto
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

    // 2. Dependencias Autowired: Inyectar el servicio de validación de tokens
    @Autowired
    private TokenService tokenService;

    /**
     * 3. Define el método adminDashboard:
     * Maneja el acceso al tablero del administrador mediante validación de token.
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        // Llama a validateToken para el rol "admin"
        Map<String, Object> validationResult = tokenService.validateToken(token, "admin");

        // Si el mapa está vacío, el token es válido
        if (validationResult.isEmpty()) {
            return "admin/adminDashboard";
        }

        // Si no está vacío, el token es inválido o el rol no coincide
        return "redirect:http://localhost:8080";
    }

    /**
     * 4. Define el método doctorDashboard:
     * Maneja el acceso al tablero del doctor mediante validación de token.
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        // Llama a validateToken para el rol "doctor"
        Map<String, Object> validationResult = tokenService.validateToken(token, "doctor");

        // Si el mapa está vacío, el token es válido
        if (validationResult.isEmpty()) {
            return "doctor/doctorDashboard";
        }

        // Si no está vacío, redirigir a la página de inicio de sesión
        return "redirect:http://localhost:8080";
    }
}