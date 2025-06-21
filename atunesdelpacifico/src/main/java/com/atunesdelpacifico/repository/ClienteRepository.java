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

    Optional<Cliente> findByIdentificacion(String identificacion);

    @Query("SELECT c FROM Cliente c WHERE c.usuario.nombreUsuario = :nombreUsuario")
    Optional<Cliente> findByUsuarioNombreUsuario(@Param("nombreUsuario") String nombreUsuario);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Cliente> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    Boolean existsByIdentificacion(String identificacion);

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.estado = :estado")
    Long countByEstado(@Param("estado") Cliente.EstadoCliente estado);

    @Query("SELECT c FROM Cliente c JOIN FETCH c.usuario WHERE c.id = :id")
    Optional<Cliente> findByIdWithUsuario(@Param("id") Long id);
}
