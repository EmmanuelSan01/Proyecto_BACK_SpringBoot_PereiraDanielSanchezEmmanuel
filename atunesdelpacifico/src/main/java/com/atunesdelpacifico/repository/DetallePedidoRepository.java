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
    
    @Query("SELECT dp FROM DetallePedido dp WHERE dp.pedido = :pedido")
    List<DetallePedido> findByPedido(@Param("pedido") Pedido pedido);
    
    @Query("SELECT dp FROM DetallePedido dp WHERE dp.lote.producto.idProducto = :productoId")
    List<DetallePedido> findByProductoId(@Param("productoId") Long productoId);
    
    @Query("SELECT SUM(dp.cantidad) FROM DetallePedido dp WHERE dp.lote.producto.idProducto = :productoId")
    Integer getTotalVendidoByProducto(@Param("productoId") Long productoId);
}
