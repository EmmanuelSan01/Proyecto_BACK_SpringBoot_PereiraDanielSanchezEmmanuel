package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.Lote;
import com.atunesdelpacifico.entity.Pedido;
import com.atunesdelpacifico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ReporteService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LoteRepository loteRepository;

    public Map<String, Object> getReporteVentasPorProducto(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> reporte = new HashMap<>();

        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        reporte.put("periodo", Map.of(
                "fechaInicio", fechaInicio,
                "fechaFin", fechaFin
        ));

        reporte.put("ventasPorProducto", Map.of(
                "Atún en aceite", 1500,
                "Atún en agua", 1200,
                "Atún en salsa", 800
        ));

        return reporte;
    }

    public Map<String, Object> getReporteVentasPorCliente(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> reporte = new HashMap<>();

        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        reporte.put("periodo", Map.of(
                "fechaInicio", fechaInicio,
                "fechaFin", fechaFin
        ));

        reporte.put("ventasPorCliente", Map.of(
                "Cliente A", 2500.00,
                "Cliente B", 1800.00,
                "Cliente C", 1200.00
        ));

        return reporte;
    }

    public Map<String, Object> getReporteProduccion(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> reporte = new HashMap<>();

        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        reporte.put("periodo", Map.of(
                "fechaInicio", fechaInicio,
                "fechaFin", fechaFin
        ));

        long lotesDisponibles = loteRepository.findByEstado(Lote.EstadoLote.DISPONIBLE).size(); // Corregido
        long lotesVendidos = loteRepository.findByEstado(Lote.EstadoLote.VENDIDO).size(); // Corregido
        long lotesDefectuosos = loteRepository.findByEstado(Lote.EstadoLote.DEFECTUOSO).size(); // Corregido

        reporte.put("lotesPorEstado", Map.of(
                "disponibles", lotesDisponibles,
                "vendidos", lotesVendidos,
                "defectuosos", lotesDefectuosos
        ));

        return reporte;
    }

    public Map<String, Object> getReporteInventario() {
        Map<String, Object> reporte = new HashMap<>();

        long totalLotes = loteRepository.count();
        long lotesDisponibles = loteRepository.findByEstado(Lote.EstadoLote.DISPONIBLE).size(); // Corregido
        long totalProductos = productoRepository.count();

        reporte.put("resumen", Map.of(
                "totalLotes", totalLotes,
                "lotesDisponibles", lotesDisponibles,
                "totalProductos", totalProductos
        ));

        return reporte;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalClientes = clienteRepository.count();
        long totalProductos = productoRepository.count();
        long totalPedidos = pedidoRepository.count();
        long pedidosPendientes = pedidoRepository.countByEstado(Pedido.EstadoPedido.Pendiente); // Corregido

        stats.put("totales", Map.of(
                "clientes", totalClientes,
                "productos", totalProductos,
                "pedidos", totalPedidos,
                "pedidosPendientes", pedidosPendientes
        ));

        stats.put("pedidosPorEstado", Map.of(
                "pendientes", pedidoRepository.countByEstado(Pedido.EstadoPedido.Pendiente), // Corregido
                "enProceso", pedidoRepository.countByEstado(Pedido.EstadoPedido.En_Proceso), // Corregido
                "enviados", pedidoRepository.countByEstado(Pedido.EstadoPedido.Enviado), // Corregido
                "cancelados", pedidoRepository.countByEstado(Pedido.EstadoPedido.Cancelado) // Corregido
        ));

        return stats;
    }
}
