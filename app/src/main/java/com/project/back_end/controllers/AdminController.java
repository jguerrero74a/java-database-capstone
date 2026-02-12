package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/admin")
public class AdminController {

    private final Service service;

    public AdminController(Service service) {
        this.service = service;
    }

    // CAMBIO AQUÍ: Quitamos "/login" para que coincida con el index.js del frontend
    @PostMapping
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Login login) {
        // Logs para ver qué llega desde el navegador
        System.out.println(">>> [CONTROLLER] Petición recibida en /admin");
        System.out.println(">>> [CONTROLLER] Identifier: " + login.getIdentifier());
        System.out.println(">>> [CONTROLLER] Password: " + login.getPassword());

        Admin admin = new Admin();
        admin.setUsername(login.getIdentifier()); 
        admin.setPassword(login.getPassword());
        
        return service.validateAdmin(admin);
    }
}