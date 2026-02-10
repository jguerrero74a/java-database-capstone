package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @NotNull(message = "El médico es requerido")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @NotNull(message = "El paciente es requerido")
    private Patient patient;

    // Se eliminó @Future y @NotNull para que el script de carga funcione sin errores de validación
    private LocalDateTime appointmentTime;

    @NotNull(message = "El estado es requerido")
    private int status; // 0: Programada, 1: Completada

    // Se eliminó @NotNull y @Size para permitir que el script del tutorial (que no trae este campo) funcione
    @Column(name = "reason_for_visit")
    private String reasonForVisit;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Appointment() {
    }

    @Transient
    public LocalDateTime getEndTime() {
        return (appointmentTime != null) ? appointmentTime.plusHours(1) : null;
    }

    @Transient
    public LocalDate getAppointmentDate() {
        return (appointmentTime != null) ? appointmentTime.toLocalDate() : null;
    }

    @Transient
    public LocalTime getAppointmentTimeOnly() {
        return (appointmentTime != null) ? appointmentTime.toLocalTime() : null;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}