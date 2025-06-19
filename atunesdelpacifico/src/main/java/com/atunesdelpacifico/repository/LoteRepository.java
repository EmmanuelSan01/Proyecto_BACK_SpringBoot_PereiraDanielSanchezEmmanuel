package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    List<Lote> findByEstado(Lote.EstadoLote estado);

    @Query("SELECT l FROM Lote l WHERE l.estado = 'DISPONIBLE' AND l.cantidadDisp > 0")
    List<Lote> findDisponibles();

    List<Lote> findByProductoIdProducto(Long productoId);

    List<Lote> findByFechaProdBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<Lote> findByFechaVencBetween(LocalDate fechaInicio, LocalDate fechaFin);

    Optional<Lote> findByCodigoLote(String codigoLote);

    @Query("SELECT l FROM Lote l WHERE l.fechaVenc <= :fecha AND l.estado = 'DISPONIBLE'")
    List<Lote> findLotesProximosAVencer(@Param("fecha") LocalDate fecha);

    @Query("SELECT SUM(l.cantidadDisp) FROM Lote l WHERE l.producto.idProducto = :productoId AND l.estado = 'DISPONIBLE'")
    Integer getTotalDisponibleByProducto(@Param("productoId") Long productoId);

    Boolean existsByCodigoLote(String codigoLote);

    @Query("SELECT COUNT(l) FROM Lote l WHERE l.estado = :estado")
    Long countByEstado(@Param("estado") Lote.EstadoLote estado);
}
