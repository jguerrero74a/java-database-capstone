package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String token) {

        if (!service.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }

        Map<String, Object> response = new HashMap<>();
        List<String> availability = doctorService.getDoctorAvailability(doctorId, date);
        response.put("availability", availability);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        if (!service.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, String> response = new HashMap<>();
        int result = doctorService.saveDoctor(doctor);
        if (result == 1) {
            response.put("message", "Doctor agregado a la base de datos");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if (result == -1) {
            response.put("message", "El doctor ya existe");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            response.put("message", "Ocurrió un error interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        if (!service.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, String> response = new HashMap<>();
        int result = doctorService.updateDoctor(doctor);
        if (result == 1) {
            response.put("message", "Doctor actualizado");
            return ResponseEntity.ok(response);
        } else if (result == -1) {
            response.put("message", "Doctor no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("message", "Ocurrió un error interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable long id,
            @PathVariable String token) {

        if (!service.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, String> response = new HashMap<>();
        int result = doctorService.deleteDoctor(id);
        if (result == 1) {
            response.put("message", "Doctor eliminado exitosamente");
            return ResponseEntity.ok(response);
        } else if (result == -1) {
            response.put("message", "Doctor no encontrado con el id");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("message", "Ocurrió un error interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        // Normalizar valores que representan nulidad
        String filterName = (name.equalsIgnoreCase("null") || name.equalsIgnoreCase("none")) ? null : name;
        String filterTime = (time.equalsIgnoreCase("null") || time.equalsIgnoreCase("none")) ? null : time;
        String filterSpec = (speciality.equalsIgnoreCase("null") || speciality.equalsIgnoreCase("none")) ? null : speciality;

        // Llamamos a la lógica unificada en DoctorService
        return ResponseEntity.ok(doctorService.filterDoctors(filterName, filterSpec, filterTime));
    }
}