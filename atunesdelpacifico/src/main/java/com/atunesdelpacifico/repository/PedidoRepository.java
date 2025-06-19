package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.Cliente;
import com.atunesdelpacifico.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstado(Pedido.EstadoPedido estado);

    List<Pedido> findByClienteIdOrderByFechaPedidoDesc(Long clienteId);

    List<Pedido> findByClienteOrderByFechaPedidoDesc(Cliente cliente);

    List<Pedido> findByFechaPedidoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    @Query("SELECT p FROM Pedido p WHERE p.cliente.usuario.nombreUsuario = :nombreUsuario ORDER BY p.fechaPedido DESC")
    List<Pedido> findByClienteUsuarioNombreUsuarioOrderByFechaPedidoDesc(@Param("nombreUsuario") String nombreUsuario);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long countByEstado(@Param("estado") Pedido.EstadoPedido estado);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado IN ('EN_PROCESO', 'ENVIADO', 'ENTREGADO')")
    Double getTotalVentas();

    @Query("SELECT p FROM Pedido p WHERE p.fechaEntrega <= :fecha AND p.estado IN ('PENDIENTE', 'EN_PROCESO')")
    List<Pedido> findPedidosVencidos(@Param("fecha") java.time.LocalDate fecha);

    Boolean existsByNumeroPedido(String numeroPedido);
}
