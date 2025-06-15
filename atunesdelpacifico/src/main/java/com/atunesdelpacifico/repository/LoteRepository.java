package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.Lote;
import com.atunesdelpacifico.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    
    @Query("SELECT l FROM Lote l WHERE l.estado = :estado")
    List<Lote> findByEstado(@Param("estado") Lote.EstadoLote estado);
    
    @Query("SELECT l FROM Lote l WHERE l.producto = :producto AND l.estado = :estado")
    List<Lote> findByProductoAndEstado(@Param("producto") Producto producto, @Param("estado") Lote.EstadoLote estado);
    
    @Query("SELECT l FROM Lote l WHERE l.fechaProduccion BETWEEN :fechaInicio AND :fechaFin")
    List<Lote> findByFechaProduccionBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT l FROM Lote l WHERE l.producto.conservante = :conservante AND l.estado = 'DISPONIBLE' AND l.cantidadDisponible > 0")
    List<Lote> findDisponiblesByConservante(@Param("conservante") Producto.TipoConservante conservante);
    
    @Query("SELECT l FROM Lote l WHERE l.cantidadDisponible > 0 AND l.estado = 'DISPONIBLE' ORDER BY l.fechaProduccion ASC")
    List<Lote> findDisponiblesOrderByFechaProduccion();
    
    @Query("SELECT SUM(l.cantidadDisponible) FROM Lote l WHERE l.producto = :producto AND l.estado = 'DISPONIBLE'")
    Integer getTotalDisponibleByProducto(@Param("producto") Producto producto);
}
