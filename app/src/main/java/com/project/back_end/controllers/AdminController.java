package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para operaciones administrativas.
 * Gestiona la autenticación y validación de administradores.
 */
@RestController
@RequestMapping("${api.path}" + "admin")
public class AdminController {

    private final Service service;

    /**
     * Inyección por constructor de la dependencia Service.
     * @param service Capa de servicio principal que orquestra la lógica de negocio.
     */
    public AdminController(Service service) {
        this.service = service;
    }

    /**
     * Endpoint para el inicio de sesión del administrador.
     * @param admin Objeto Admin que contiene las credenciales (username y password).
     * @return ResponseEntity con un token JWT si es exitoso o mensaje de error.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        // Delega la validación y generación del token a la capa de servicio
        return service.validateAdmin(admin);
    }
}