package com.project.back_end.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    @NotNull(message = "El nombre del paciente es requerido")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String patientName;

    @NotNull(message = "El ID de la cita es requerido")
    private Long appointmentId;

    @NotNull(message = "El nombre del medicamento es requerido")
    @Size(min = 3, max = 100, message = "El medicamento debe tener entre 3 y 100 caracteres")
    private String medication;

    @NotNull(message = "La dosificación es requerida")
    @Size(min = 3, max = 20, message = "La dosificación debe tener entre 3 y 20 caracteres")
    private String dosage;

    @Size(max = 200, message = "Las notas no pueden exceder los 200 caracteres")
    private String doctorNotes;

    @Min(0)
    private int refillCount;

    @Size(max = 100)
    private String pharmacyName;

    @JsonIgnore // Ejemplo de campo interno que no se envía al cliente
    private String internalSystemCode;

    // Constructor vacío requerido por Spring Data
    public Prescription() {
    }

    // Constructor para inicializar campos importantes
    public Prescription(String patientName, Long appointmentId, String medication, String dosage) {
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
    }

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
public int getRefillCount() {
        return refillCount;
    }

    public void setRefillCount(int refillCount) {
        this.refillCount = refillCount;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getInternalSystemCode() {
        return internalSystemCode;
    }

    public void setInternalSystemCode(String internalSystemCode) {
        this.internalSystemCode = internalSystemCode;
    }    
}