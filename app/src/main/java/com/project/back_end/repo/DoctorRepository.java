package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Doctor.
 * Proporciona acceso a datos mediante Spring Data JPA para la gestión de médicos.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 1. Encuentra un doctor por su dirección de correo electrónico
    Doctor findByEmail(String email);

    // 2. Encuentra doctores por coincidencia parcial de nombre usando @Query
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(@Param("name") String name);

    // 3. Filtra doctores por nombre parcial y especialidad exacta (ignora mayúsculas/minúsculas)
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
            @Param("name") String name, 
            @Param("specialty") String specialty);

    // 4. Encuentra doctores por especialidad, ignorando mayúsculas o minúsculas
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}