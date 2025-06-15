package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.entity.Cliente;
import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.model.dto.ClienteRequest;
import com.atunesdelpacifico.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cliente")
@Tag(name = "Clientes", description = "Gestión de clientes")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Listar todos los clientes", description = "Obtiene la lista completa de clientes")
    public ResponseEntity<ApiResponse<List<Cliente>>> getAllClientes() {
        try {
            List<Cliente> clientes = clienteService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Clientes obtenidos exitosamente", clientes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener clientes: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'CLIENTE')")
    @Operation(summary = "Obtener cliente por ID", description = "Obtiene un cliente específico por su ID")
    public ResponseEntity<ApiResponse<Cliente>> getClienteById(@PathVariable Long id) {
        try {
            Optional<Cliente> cliente = clienteService.findById(id);
            if (cliente.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Cliente encontrado", cliente.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Cliente no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener cliente: " + e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Crear nuevo cliente", description = "Registra un nuevo cliente en el sistema")
    public ResponseEntity<ApiResponse<Cliente>> createCliente(@Valid @RequestBody ClienteRequest clienteRequest) {
        try {
            Cliente nuevoCliente = clienteService.save(clienteRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Cliente creado exitosamente", nuevoCliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear cliente: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente")
    public ResponseEntity<ApiResponse<Cliente>> updateCliente(@PathVariable Long id, 
                                                            @Valid @RequestBody ClienteRequest clienteRequest) {
        try {
            Cliente clienteActualizado = clienteService.update(id, clienteRequest);
            return ResponseEntity.ok(ApiResponse.success("Cliente actualizado exitosamente", clienteActualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al actualizar cliente: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema")
    public ResponseEntity<ApiResponse<String>> deleteCliente(@PathVariable Long id) {
        try {
            clienteService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Cliente eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al eliminar cliente: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Cambiar estado del cliente", description = "Activa o desactiva un cliente")
    public ResponseEntity<ApiResponse<Cliente>> cambiarEstadoCliente(@PathVariable Long id, 
                                                                   @RequestParam Cliente.EstadoCliente estado) {
        try {
            Cliente cliente = clienteService.cambiarEstado(id, estado);
            return ResponseEntity.ok(ApiResponse.success("Estado del cliente actualizado", cliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al cambiar estado: " + e.getMessage()));
        }
    }
    
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Buscar clientes por nombre", description = "Busca clientes que contengan el texto en su nombre")
    public ResponseEntity<ApiResponse<List<Cliente>>> buscarClientesPorNombre(@RequestParam String nombre) {
        try {
            List<Cliente> clientes = clienteService.buscarPorNombre(nombre);
            return ResponseEntity.ok(ApiResponse.success("Búsqueda completada", clientes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en la búsqueda: " + e.getMessage()));
        }
    }
    
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Obtener clientes activos", description = "Obtiene todos los clientes con estado activo")
    public ResponseEntity<ApiResponse<List<Cliente>>> getClientesActivos() {
        try {
            List<Cliente> clientes = clienteService.findByEstado(Cliente.EstadoCliente.ACTIVO);
            return ResponseEntity.ok(ApiResponse.success("Clientes activos obtenidos", clientes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener clientes activos: " + e.getMessage()));
        }
    }
}
