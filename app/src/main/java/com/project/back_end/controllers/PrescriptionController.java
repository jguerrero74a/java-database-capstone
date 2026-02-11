package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para la gestión de recetas médicas (MongoDB).
 * Solo accesible para usuarios con rol de médico.
 */
@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;

    public PrescriptionController(PrescriptionService prescriptionService, Service service) {
        this.prescriptionService = prescriptionService;
        this.service = service;
    }

    /**
     * 1. Guardar una nueva receta.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token, 
            @RequestBody Prescription prescription) {

        // CORRECCIÓN: validateToken devuelve boolean
        if (!service.validateToken(token, "doctor")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Unauthorized: Doctor token invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return prescriptionService.savePrescription(prescription);
    }

    /**
     * 2. Obtener receta por ID de cita.
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId, 
            @PathVariable String token) {

        // CORRECCIÓN: validateToken devuelve boolean
        if (!service.validateToken(token, "doctor")) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("message", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMap);
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}