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
    Optional<Cliente> findByIdentificacion(String identificacion);
    boolean existsByIdentificacion(String identificacion);
    
    @Query("SELECT c FROM Cliente c WHERE c.estado = :estado")
    List<Cliente> findByEstado(@Param("estado") Cliente.EstadoCliente estado);
    
    @Query("SELECT c FROM Cliente c WHERE c.tipo = :tipo")
    List<Cliente> findByTipo(@Param("tipo") Cliente.TipoCliente tipo);
    
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Cliente> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
}
