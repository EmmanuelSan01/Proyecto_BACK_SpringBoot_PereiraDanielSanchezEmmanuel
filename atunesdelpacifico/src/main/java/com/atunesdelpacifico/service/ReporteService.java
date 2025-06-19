package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.Lote;
import com.atunesdelpacifico.entity.Pedido;
import com.atunesdelpacifico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReporteService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    public Map<String, Object> getReporteVentasPorProducto(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> reporte = new HashMap<>();

        // Implementar lógica de reporte de ventas por producto
        reporte.put("titulo", "Reporte de Ventas por Producto");
        reporte.put("fechaInicio", fechaInicio);
        reporte.put("fechaFin", fechaFin);
        reporte.put("datos", "Datos del reporte"); // Implementar consulta específica

        return reporte;
    }

    public Map<String, Object> getReporteVentasPorCliente(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> reporte = new HashMap<>();

        reporte.put("titulo", "Reporte de Ventas por Cliente");
        reporte.put("fechaInicio", fechaInicio);
        reporte.put("fechaFin", fechaFin);
        reporte.put("datos", "Datos del reporte"); // Implementar consulta específica

        return reporte;
    }

    public Map<String, Object> getReporteProduccion(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> reporte = new HashMap<>();

        reporte.put("titulo", "Reporte de Producción");
        reporte.put("fechaInicio", fechaInicio);
        reporte.put("fechaFin", fechaFin);
        reporte.put("datos", "Datos del reporte"); // Implementar consulta específica

        return reporte;
    }

    public Map<String, Object> getReporteInventario() {
        Map<String, Object> reporte = new HashMap<>();

        Long totalLotes = loteRepository.count();
        Long lotesDisponibles = loteRepository.countByEstado(Lote.EstadoLote.DISPONIBLE);
        Long lotesVendidos = loteRepository.countByEstado(Lote.EstadoLote.VENDIDO);
        Long lotesDefectuosos = loteRepository.countByEstado(Lote.EstadoLote.DEFECTUOSO);

        reporte.put("titulo", "Reporte de Inventario");
        reporte.put("totalLotes", totalLotes);
        reporte.put("lotesDisponibles", lotesDisponibles);
        reporte.put("lotesVendidos", lotesVendidos);
        reporte.put("lotesDefectuosos", lotesDefectuosos);

        return reporte;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Estadísticas generales
        Long totalClientes = clienteRepository.count();
        Long totalProductos = productoRepository.count();
        Long totalPedidos = pedidoRepository.count();
        Long pedidosPendientes = pedidoRepository.countByEstado(Pedido.EstadoPedido.PENDIENTE);
        Long pedidosEnProceso = pedidoRepository.countByEstado(Pedido.EstadoPedido.EN_PROCESO);

        stats.put("totalClientes", totalClientes);
        stats.put("totalProductos", totalProductos);
        stats.put("totalPedidos", totalPedidos);
        stats.put("pedidosPendientes", pedidosPendientes);
        stats.put("pedidosEnProceso", pedidosEnProceso);

        return stats;
    }
}
