package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.DetallePedido;
import com.atunesdelpacifico.entity.Pedido;
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
    List<DetallePedido> findByPedidoId(@Param("pedidoId") Long pedidoId);

    @Query("SELECT SUM(dp.cantidad) FROM DetallePedido dp WHERE dp.lote.idLote = :loteId")
    Integer getTotalCantidadVendidaByLote(@Param("loteId") Long loteId);

    @Query("SELECT dp FROM DetallePedido dp JOIN dp.pedido p WHERE p.estado IN ('EN_PROCESO', 'ENVIADO', 'ENTREGADO')")
    List<DetallePedido> findDetallesVendidos();
}
