package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    /**
     * 3. Recupera citas para un doctor en una fecha específica.
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String patientName,
            @PathVariable String token) {

        // CORRECCIÓN: validateToken devuelve boolean. Si es falso, retornamos UNAUTHORIZED.
        if (!service.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token for doctor role");
        }

        String filterName = (patientName.equalsIgnoreCase("none")) ? null : patientName;
        return ResponseEntity.ok(appointmentService.getAppointment(filterName, date, token));
    }

    /**
     * 4. Reserva una nueva cita.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        // CORRECCIÓN: Validar token (boolean)
        if (!service.validateToken(token, "patient")) {
            response.put("message", "Invalid token for patient role");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // CORRECCIÓN: Manejar el hecho de que validateAppointment ahora devuelve un ResponseEntity
        // Para que tu lógica de 'validationCode' funcione, debemos llamar al servicio de forma que nos dé el código
        // O adaptar la lógica. Aquí lo adaptamos para que use el servicio correctamente:
        ResponseEntity<Map<String, String>> validationResponse = service.validateAppointment(appointment);
        
        if (validationResponse.getStatusCode() == HttpStatus.OK) {
            int result = appointmentService.bookAppointment(appointment);
            if (result == 1) {
                response.put("message", "Appointment booked successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("message", "Error booking appointment");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            // Si la validación falla, devolvemos el error que envió el servicio
            return validationResponse;
        }
    }

    /**
     * 5. Actualiza una cita existente.
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        if (!service.validateToken(token, "patient")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return appointmentService.updateAppointment(appointment);
    }

    /**
     * 6. Cancela una cita específica.
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token) {

        if (!service.validateToken(token, "patient")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return appointmentService.cancelAppointment(id, token);
    }
}