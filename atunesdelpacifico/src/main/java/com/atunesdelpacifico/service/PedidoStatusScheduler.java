package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.Pedido;
import com.atunesdelpacifico.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoStatusScheduler {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Scheduled(fixedRate = 300000) // Ejecutar cada 5 minutos
    public void actualizarEstadoPedidos() {
        LocalDateTime fechaLimite = LocalDateTime.now().minusHours(24);

        List<Pedido> pedidosParaActualizar = pedidoRepository.findPedidosParaActualizacion(fechaLimite);

        for (Pedido pedido : pedidosParaActualizar) {
            // Lógica para actualizar el estado del pedido
            switch (pedido.getEstado()) {
                case PENDIENTE:
                    pedido.setEstado(Pedido.EstadoPedido.EN_PROCESO);
                    break;
                case EN_PROCESO:
                    pedido.setEstado(Pedido.EstadoPedido.ENVIADO);
                    break;
                case ENVIADO:
                    pedido.setEstado(Pedido.EstadoPedido.ENTREGADO);
                    break;
                default:
                    break;
            }
            pedidoRepository.save(pedido);
        }
    }
}
