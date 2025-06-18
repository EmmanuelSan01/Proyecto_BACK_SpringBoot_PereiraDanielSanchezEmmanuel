package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.entity.Lote;
import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.service.LoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/operador/lotes")
@Tag(name = "Lotes", description = "Gestión de lotes de producción e inventario")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LoteController {
    
    @Autowired
    private LoteService loteService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Listar todos los lotes", description = "Obtiene la lista completa de lotes de producción")
    public ResponseEntity<ApiResponse<List<Lote>>> getAllLotes() {
        try {
            List<Lote> lotes = loteService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Lotes obtenidos exitosamente", lotes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener lotes: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Obtener lote por ID", description = "Obtiene un lote específico por su ID")
    public ResponseEntity<ApiResponse<Lote>> getLoteById(@PathVariable Long id) {
        try {
            Optional<Lote> lote = loteService.findById(id);
            if (lote.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Lote encontrado", lote.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Lote no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener lote: " + e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Crear nuevo lote", description = "Registra un nuevo lote de producción")
    public ResponseEntity<ApiResponse<Lote>> createLote(@Valid @RequestBody Lote lote) {
        try {
            Lote nuevoLote = loteService.save(lote);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Lote creado exitosamente", nuevoLote));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear lote: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Actualizar lote", description = "Actualiza los datos de un lote existente")
    public ResponseEntity<ApiResponse<Lote>> updateLote(@PathVariable Long id, 
                                                       @Valid @RequestBody Lote lote) {
        try {
            Lote loteActualizado = loteService.update(id, lote);
            return ResponseEntity.ok(ApiResponse.success("Lote actualizado exitosamente", loteActualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al actualizar lote: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar lote", description = "Elimina un lote del sistema")
    public ResponseEntity<ApiResponse<String>> deleteLote(@PathVariable Long id) {
        try {
            loteService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Lote eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al eliminar lote: " + e.getMessage()));
        }
    }
    
    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'CLIENTE')")
    @Operation(summary = "Obtener lotes disponibles", description = "Lista todos los lotes con inventario disponible")
    public ResponseEntity<ApiResponse<List<Lote>>> getLotesDisponibles() {
        try {
            List<Lote> lotes = loteService.findDisponibles();
            return ResponseEntity.ok(ApiResponse.success("Lotes disponibles obtenidos", lotes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener lotes disponibles: " + e.getMessage()));
        }
    }
    
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Obtener lotes por estado", description = "Filtra lotes por su estado")
    public ResponseEntity<ApiResponse<List<Lote>>> getLotesByEstado(@PathVariable Lote.EstadoLote estado) {
        try {
            List<Lote> lotes = loteService.findByEstado(estado);
            return ResponseEntity.ok(ApiResponse.success("Lotes filtrados exitosamente", lotes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al filtrar lotes: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Cambiar estado del lote", description = "Actualiza el estado de un lote")
    public ResponseEntity<ApiResponse<Lote>> cambiarEstadoLote(@PathVariable Long id, 
                                                              @RequestParam Lote.EstadoLote estado) {
        try {
            Lote lote = loteService.cambiarEstado(id, estado);
            return ResponseEntity.ok(ApiResponse.success("Estado del lote actualizado", lote));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al cambiar estado: " + e.getMessage()));
        }
    }
    
    @GetMapping("/fecha-produccion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Obtener lotes por rango de fechas", description = "Filtra lotes por fecha de producción")
    public ResponseEntity<ApiResponse<List<Lote>>> getLotesByFechaProduccion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            List<Lote> lotes = loteService.findByFechaProduccion(fechaInicio, fechaFin);
            return ResponseEntity.ok(ApiResponse.success("Lotes filtrados por fecha", lotes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al filtrar lotes por fecha: " + e.getMessage()));
        }
    }
}
