package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByEstado(Cliente.EstadoCliente estado);

    List<Cliente> findByTipo(Cliente.TipoCliente tipo);

    Optional<Cliente> findByUsuarioNombreUsuario(String nombreUsuario);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Cliente> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    boolean existsByIdentificacion(String identificacion);

    Optional<Cliente> findByIdentificacion(String identificacion);
}
