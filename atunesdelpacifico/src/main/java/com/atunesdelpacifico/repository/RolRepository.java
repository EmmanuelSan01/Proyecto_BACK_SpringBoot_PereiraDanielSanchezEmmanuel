package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Byte> {
    Optional<Rol> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
