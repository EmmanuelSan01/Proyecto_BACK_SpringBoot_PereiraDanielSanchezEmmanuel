package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.DetallePedido;
import com.atunesdelpacifico.entity.Pedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedido(Pedido pedido);

    List<DetallePedido> findByLoteIdLote(Long loteId);

    @Query("SELECT dp FROM DetallePedido dp WHERE dp.pedido.id = :pedidoId")
    List<DetallePedido> findByPedidoId(Long pedidoId);

    @Query("SELECT SUM(dp.cantidad) FROM DetallePedido dp WHERE dp.lote.idLote = :loteId")
    Integer getTotalCantidadVendidaByLote(@Param("loteId") Long loteId);

    @Query("SELECT dp FROM DetallePedido dp JOIN dp.pedido p WHERE p.estado IN ('EN_PROCESO', 'ENVIADO', 'ENTREGADO')")
    List<DetallePedido> findDetallesVendidos();

    @Query("SELECT l.producto.nombre, SUM(dp.cantidad) as totalVendido " +
            "FROM DetallePedido dp " +
            "JOIN dp.lote l " +
            "JOIN dp.pedido p " +
            "WHERE p.estado = 'ENTREGADO' " +
            "GROUP BY l.producto.nombre " +
            "ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos(Pageable pageable);

    @Query("SELECT dp FROM DetallePedido dp WHERE dp.lote.id = :loteId")
    List<DetallePedido> findByLoteId(@Param("loteId") Long loteId);

    @Query("SELECT SUM(dp.cantidad) FROM DetallePedido dp " +
            "JOIN dp.pedido p WHERE p.estado = 'ENTREGADO'")
    Long getTotalProductosVendidos();

    // ✅ QUERY CORREGIDO - Usar idPedido en lugar de id
    @Query("SELECT dp FROM DetallePedido dp " +
            "JOIN FETCH dp.lote l " +
            "JOIN FETCH l.producto " +
            "WHERE dp.pedido.idPedido = :pedidoId")
    List<DetallePedido> findByPedidoIdWithLoteAndProduct(@Param("pedidoId") Long pedidoId);

    // Método por defecto para obtener productos más vendidos
    default List<Object[]> findProductosMasVendidos() {
        return findProductosMasVendidos(Pageable.ofSize(10));
    }

    @Query("SELECT SUM(dp.subtotal) FROM DetallePedido dp JOIN dp.pedido p WHERE p.estado IN ('ENTREGADO', 'ENVIADO')")
    Double getTotalVentasFromDetalles();

    // ✅ NUEVO QUERY PARA DEBUG - Obtener detalles con información completa
    @Query("SELECT dp.idDetalle, dp.cantidad, dp.precioUnitario, dp.subtotal, " +
            "l.idLote, l.codigoLote, " +
            "p.idProducto, p.nombre, p.descripcion, p.conservante " +
            "FROM DetallePedido dp " +
            "JOIN dp.lote l " +
            "JOIN l.producto p " +
            "WHERE dp.pedido.idPedido = :pedidoId")
    List<Object[]> findDetallesPedidoCompleto(@Param("pedidoId") Long pedidoId);
}
