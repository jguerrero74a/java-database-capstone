package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository, 
                         AppointmentRepository appointmentRepository, 
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<String> allSlots = Arrays.asList("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");
        
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        
        List<String> bookedSlots = bookedAppointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            if (!doctorRepository.existsById(doctor.getId())) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) return -1;
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());

        if (doctor != null && doctor.getPassword().equals(login.getPassword())) {
            String token = tokenService.createToken(doctor.getId(), "DOCTOR");
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        
        response.put("message", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // --- LÓGICA DE FILTRADO UNIFICADA ---

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctors(String name, String specialty, String time) {
        List<Doctor> doctors = doctorRepository.findAll();

        List<Doctor> filteredDoctors = doctors.stream()
            .filter(d -> (name == null || d.getName().toLowerCase().contains(name.toLowerCase())))
            .filter(d -> (specialty == null || d.getSpecialty().equalsIgnoreCase(specialty)))
            .filter(d -> (time == null || isTimeAvailable(d.getAvailableTimes(), time)))
            .collect(Collectors.toList());

        Map<String, Object> res = new HashMap<>();
        res.put("doctors", filteredDoctors);
        return res;
    }

    private boolean isTimeAvailable(List<String> availableTimes, String timeFilter) {
        if (availableTimes == null || availableTimes.isEmpty()) return false;
        
        // Manejo de AM/PM
        if (timeFilter.equalsIgnoreCase("AM")) {
            return availableTimes.stream().anyMatch(t -> {
                int hour = Integer.parseInt(t.split(":")[0]);
                return hour < 12;
            });
        } else if (timeFilter.equalsIgnoreCase("PM")) {
            return availableTimes.stream().anyMatch(t -> {
                int hour = Integer.parseInt(t.split(":")[0]);
                return hour >= 12;
            });
        }
        
        // Manejo de rangos exactos (ej: "09:00-10:00") o coincidencias parciales
        return availableTimes.stream().anyMatch(t -> t.equalsIgnoreCase(timeFilter));
    }
}