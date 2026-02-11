package com.project.back_end.repo;

import com.project.back_end.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Admin.
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas sobre MySQL.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Busca un administrador basado en su nombre de usuario.
     * Spring Data JPA genera automáticamente la consulta SQL: 
     * SELECT * FROM admins WHERE username = ?
     * * @param username El nombre de usuario a buscar.
     * @return El objeto Admin si existe, de lo contrario null.
     */
    Admin findByUsername(String username);
}