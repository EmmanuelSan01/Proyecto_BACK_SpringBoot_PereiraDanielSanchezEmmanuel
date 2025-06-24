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

    // Buscar por estado del lote
    List<Lote> findByEstado(Lote.EstadoLote estado);

    // Buscar lotes disponibles con stock
    @Query("SELECT l FROM Lote l WHERE l.estado = 'DISPONIBLE' AND l.cantidadDisp > 0")
    List<Lote> findDisponibles();

    // Buscar lotes por ID de producto (CORREGIDO)
    @Query("SELECT l FROM Lote l WHERE l.producto.idProducto = :productoId")
    List<Lote> findByProductoIdProducto(@Param("productoId") Long productoId);

    // Buscar por rango de fechas de producción
    List<Lote> findByFechaProdBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Buscar por rango de fechas de vencimiento
    List<Lote> findByFechaVencBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Buscar por código de lote
    Optional<Lote> findByCodigoLote(String codigoLote);

    // Buscar lotes próximos a vencer
    @Query("SELECT l FROM Lote l WHERE l.fechaVenc <= :fecha AND l.estado = 'DISPONIBLE'")
    List<Lote> findLotesProximosAVencer(@Param("fecha") LocalDate fecha);

    // Obtener total disponible por producto
    @Query("SELECT COALESCE(SUM(l.cantidadDisp), 0) FROM Lote l WHERE l.producto.idProducto = :productoId AND l.estado = 'DISPONIBLE'")
    Integer getTotalDisponibleByProducto(@Param("productoId") Long productoId);

    // Verificar si existe código de lote
    Boolean existsByCodigoLote(String codigoLote);

    // Contar por estado
    @Query("SELECT COUNT(l) FROM Lote l WHERE l.estado = :estado")
    Long countByEstado(@Param("estado") Lote.EstadoLote estado);

    // Contar lotes vencidos
    @Query("SELECT COUNT(l) FROM Lote l WHERE l.fechaVenc < :fecha")
    Long countByFechaVencBefore(@Param("fecha") LocalDate fecha);

    // Total de inventario disponible
    @Query("SELECT COALESCE(SUM(l.cantidadDisp), 0) FROM Lote l WHERE l.estado = 'DISPONIBLE'")
    Long getTotalInventarioDisponible();

    // Lotes con stock bajo
    @Query("SELECT l FROM Lote l WHERE l.cantidadDisp < 100 AND l.estado = 'DISPONIBLE'")
    List<Lote> findLotesConStockBajo();

    // Buscar lote con producto (JOIN FETCH para evitar lazy loading)
    @Query("SELECT l FROM Lote l JOIN FETCH l.producto WHERE l.codigoLote = :codigo")
    Optional<Lote> findByCodigoLoteWithProduct(@Param("codigo") String codigoLote);

    // Buscar lotes por estado con producto
    @Query("SELECT l FROM Lote l JOIN FETCH l.producto WHERE l.estado = :estado")
    List<Lote> findByEstadoWithProduct(@Param("estado") Lote.EstadoLote estado);
}
