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

    // 2. Inyección por Constructor
    public DoctorService(DoctorRepository doctorRepository, 
                         AppointmentRepository appointmentRepository, 
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 4. Obtener disponibilidad del doctor
    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        // Definimos slots de 9 AM a 5 PM
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

    // 5. Guardar Doctor
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1; // Conflicto: ya existe
            }
            doctorRepository.save(doctor);
            return 1; // Éxito
        } catch (Exception e) {
            return 0; // Error interno
        }
    }

    // 6. Actualizar Doctor
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

    // 7. Obtener todos los doctores
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // 8. Eliminar Doctor
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

    // 9. Validar Doctor (Login)
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

    // 10. Buscar por nombre
    @Transactional(readOnly = true)
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", doctorRepository.findByNameLike(name));
        return res;
    }

    // 11. Filtrar por Nombre, Especialidad y Tiempo (AM/PM)
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return res;
    }

    // 13. Filtrar por Nombre y Tiempo
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return res;
    }

    // 14. Filtrar por Nombre y Especialidad
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty));
        return res;
    }

    // 15. Filtrar por Tiempo y Especialidad
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return res;
    }

    // 16. Filtrar por Especialidad
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", doctorRepository.findBySpecialtyIgnoreCase(specialty));
        return res;
    }

    // 17. Filtrar todos por Tiempo
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return res;
    }

    // 12. Lógica privada de filtrado AM/PM
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream().filter(d -> {
            // Suponiendo que d.getAvailableTimes() devuelve una lista de Strings tipo "HH:mm"
            List<String> times = d.getAvailableTimes(); 
            if (times == null) return false;
            if (amOrPm.equalsIgnoreCase("AM")) {
                return times.stream().anyMatch(t -> Integer.parseInt(t.split(":")[0]) < 12);
            } else {
                return times.stream().anyMatch(t -> Integer.parseInt(t.split(":")[0]) >= 12);
            }
        }).collect(Collectors.toList());
    }
}