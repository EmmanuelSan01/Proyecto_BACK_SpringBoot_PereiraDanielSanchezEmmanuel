package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.Cliente;
import com.atunesdelpacifico.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    @Query("SELECT p FROM Pedido p WHERE p.cliente = :cliente ORDER BY p.fechaPedido DESC")
    List<Pedido> findByClienteOrderByFechaPedidoDesc(@Param("cliente") Cliente cliente);
    
    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado")
    List<Pedido> findByEstado(@Param("estado") Pedido.EstadoPedido estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    List<Pedido> findByFechaPedidoBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT p FROM Pedido p WHERE p.cliente.idUsuario = :clienteId ORDER BY p.fechaPedido DESC")
    List<Pedido> findByClienteIdOrderByFechaPedidoDesc(@Param("clienteId") Long clienteId);
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long countByEstado(@Param("estado") Pedido.EstadoPedido estado);
}
