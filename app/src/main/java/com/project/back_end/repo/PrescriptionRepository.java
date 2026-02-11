package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Prescription.
 * Utiliza Spring Data MongoDB para gestionar el almacenamiento de recetas médicas.
 */
@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    /**
     * Recupera una lista de recetas asociadas a un ID de cita específico.
     * Al ser MongoDB, Spring generará automáticamente la consulta sobre la colección.
     * * @param appointmentId El ID de la cita relacionada.
     * @return Una lista de documentos Prescription que coincidan con el ID.
     */
    List<Prescription> findByAppointmentId(Long appointmentId);
}