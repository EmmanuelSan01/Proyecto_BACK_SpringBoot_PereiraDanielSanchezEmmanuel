package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.model.dto.DashboardStatsDTO;
import com.atunesdelpacifico.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
@Tag(name = "Reportes", description = "Generación de reportes y estadísticas")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/ventas-producto")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Reporte de ventas por producto", description = "Genera reporte de ventas agrupadas por producto")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReporteVentasPorProducto(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            Map<String, Object> reporte = reporteService.getReporteVentasPorProducto(fechaInicio, fechaFin);
            return ResponseEntity.ok(ApiResponse.success("Reporte generado exitosamente", reporte));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al generar reporte: " + e.getMessage()));
        }
    }

    @GetMapping("/ventas-cliente")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Reporte de ventas por cliente", description = "Genera reporte de ventas agrupadas por cliente")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReporteVentasPorCliente(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            Map<String, Object> reporte = reporteService.getReporteVentasPorCliente(fechaInicio, fechaFin);
            return ResponseEntity.ok(ApiResponse.success("Reporte generado exitosamente", reporte));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al generar reporte: " + e.getMessage()));
        }
    }

    @GetMapping("/produccion")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Reporte de producción", description = "Genera reporte de producción y lotes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReporteProduccion(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            Map<String, Object> reporte = reporteService.getReporteProduccion(fechaInicio, fechaFin);
            return ResponseEntity.ok(ApiResponse.success("Reporte generado exitosamente", reporte));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al generar reporte: " + e.getMessage()));
        }
    }

    @GetMapping("/inventario")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Reporte de inventario", description = "Genera reporte del estado actual del inventario")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReporteInventario() {
        try {
            Map<String, Object> reporte = reporteService.getReporteInventario();
            return ResponseEntity.ok(ApiResponse.success("Reporte generado exitosamente", reporte));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al generar reporte: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Estadísticas del dashboard", description = "Obtiene estadísticas generales para el dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {
        try {
            System.out.println("=== Iniciando getDashboardStats ===");
            DashboardStatsDTO stats = reporteService.getDashboardStats();
            System.out.println("=== Stats generadas exitosamente ===");
            return ResponseEntity.ok(ApiResponse.success("Estadísticas obtenidas exitosamente", stats));
        } catch (Exception e) {
            System.err.println("=== Error en getDashboardStats: " + e.getMessage() + " ===");
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    @GetMapping("/notificaciones")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Obtener notificaciones del sistema", description = "Obtiene notificaciones sobre el estado del sistema")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotificaciones() {
        try {
            System.out.println("=== Iniciando getNotificaciones ===");
            Map<String, Object> notificaciones = reporteService.getNotificaciones();
            System.out.println("=== Notificaciones generadas exitosamente ===");
            return ResponseEntity.ok(ApiResponse.success("Notificaciones obtenidas exitosamente", notificaciones));
        } catch (Exception e) {
            System.err.println("=== Error en getNotificaciones: " + e.getMessage() + " ===");
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Error al obtener notificaciones: " + e.getMessage()));
        }
    }
}
