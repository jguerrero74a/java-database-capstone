package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Patient.
 * Proporciona acceso a los datos de los pacientes en MySQL a través de Spring Data JPA.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Busca un paciente por su dirección de correo electrónico.
     * @param email El correo electrónico del paciente.
     * @return El objeto Patient si se encuentra, de lo contrario null.
     */
    Patient findByEmail(String email);

    /**
     * Busca un paciente que coincida con el correo electrónico o el número de teléfono proporcionado.
     * Útil para procesos de validación de registros duplicados o recuperación de cuentas.
     * @param email El correo electrónico del paciente.
     * @param phone El número de teléfono del paciente.
     * @return El objeto Patient si coincide con alguno de los parámetros.
     */
    Patient findByEmailOrPhone(String email, String phone);
}