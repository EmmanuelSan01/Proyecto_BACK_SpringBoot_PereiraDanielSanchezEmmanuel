package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.Cliente;
import com.atunesdelpacifico.entity.Pedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteIdOrderByFechaPedidoDesc(Long clienteId);

    List<Pedido> findByClienteOrderByFechaPedidoDesc(Cliente cliente);

    @Query("SELECT p FROM Pedido p WHERE p.cliente.usuario.nombreUsuario = :nombreUsuario ORDER BY p.fechaPedido DESC")
    List<Pedido> findByClienteUsuarioNombreUsuarioOrderByFechaPedidoDesc(@Param("nombreUsuario") String nombreUsuario);

    @Query("SELECT p FROM Pedido p WHERE p.fechaEntrega <= :fecha AND p.estado IN ('PENDIENTE', 'EN_PROCESO')")
    List<Pedido> findPedidosVencidos(@Param("fecha") java.time.LocalDate fecha);

    Boolean existsByNumeroPedido(String numeroPedido);

    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('PENDIENTE', 'EN_PROCESO', 'ENVIADO') ORDER BY p.fechaPedido ASC")
    List<Pedido> findPedidosParaActualizacion(LocalDateTime fechaLimite);

    @Query("SELECT p FROM Pedido p ORDER BY p.fechaPedido DESC")
    List<Pedido> findTop10ByOrderByFechaPedidoDesc(Pageable pageable);

    List<Pedido> findByEstado(Pedido.EstadoPedido estado);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long countByEstado(@Param("estado") Pedido.EstadoPedido estado);

    List<Pedido> findByFechaPedidoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    @Query("SELECT COALESCE(SUM(p.total), 0.0) FROM Pedido p WHERE p.estado = 'ENTREGADO'")
    Double getTotalVentas();

    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.cliente.id = :clienteId")
    List<Pedido> findByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT p FROM Pedido p WHERE p.numeroPedido = :numeroPedido")
    Pedido findByNumeroPedido(@Param("numeroPedido") String numeroPedido);

    @Query("SELECT p FROM Pedido p WHERE YEAR(p.fechaPedido) = :year AND MONTH(p.fechaPedido) = :month")
    List<Pedido> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
}
