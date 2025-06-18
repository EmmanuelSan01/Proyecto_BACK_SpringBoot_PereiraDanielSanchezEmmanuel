package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.entity.Cliente;
import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.model.dto.ClienteRequest;
import com.atunesdelpacifico.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
@Tag(name = "Clientes", description = "Endpoints para gestión de clientes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/registrar")
    @Operation(summary = "Registrar nuevo cliente", description = "Registra un nuevo cliente con su usuario asociado")
    public ResponseEntity<ApiResponse<Cliente>> registrarCliente(@Valid @RequestBody ClienteRequest clienteRequest) {
        try {
            Cliente cliente = clienteService.registrarCliente(clienteRequest);
            return ResponseEntity.ok(ApiResponse.success("Cliente registrado exitosamente", cliente));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al registrar cliente: " + e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Listar todos los clientes", description = "Obtiene la lista de todos los clientes")
    public ResponseEntity<ApiResponse<List<Cliente>>> listarClientes() {
        try {
            List<Cliente> clientes = clienteService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Clientes obtenidos exitosamente", clientes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener clientes: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR') or (hasRole('CLIENTE') and @clienteService.isOwner(#id, authentication.name))")
    @Operation(summary = "Obtener cliente por ID", description = "Obtiene un cliente específico por su ID")
    public ResponseEntity<ApiResponse<Cliente>> obtenerCliente(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            return ResponseEntity.ok(ApiResponse.success("Cliente obtenido exitosamente", cliente));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener cliente: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR') or (hasRole('CLIENTE') and @clienteService.isOwner(#id, authentication.name))")
    @Operation(summary = "Actualizar cliente", description = "Actualiza la información de un cliente")
    public ResponseEntity<ApiResponse<Cliente>> actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequest clienteRequest) {
        try {
            Cliente cliente = clienteService.update(id, clienteRequest);
            return ResponseEntity.ok(ApiResponse.success("Cliente actualizado exitosamente", cliente));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al actualizar cliente: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema")
    public ResponseEntity<ApiResponse<String>> eliminarCliente(@PathVariable Long id) {
        try {
            clienteService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Cliente eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al eliminar cliente: " + e.getMessage()));
        }
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Listar clientes activos", description = "Obtiene la lista de clientes con estado activo")
    public ResponseEntity<ApiResponse<List<Cliente>>> listarClientesActivos() {
        try {
            List<Cliente> clientes = clienteService.findByEstado(Cliente.EstadoCliente.ACTIVO);
            return ResponseEntity.ok(ApiResponse.success("Clientes activos obtenidos exitosamente", clientes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener clientes activos: " + e.getMessage()));
        }
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Listar clientes por tipo", description = "Obtiene la lista de clientes por tipo")
    public ResponseEntity<ApiResponse<List<Cliente>>> listarClientesPorTipo(@PathVariable Cliente.TipoCliente tipo) {
        try {
            List<Cliente> clientes = clienteService.findByTipoCliente(tipo);
            return ResponseEntity.ok(ApiResponse.success("Clientes por tipo obtenidos exitosamente", clientes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener clientes por tipo: " + e.getMessage()));
        }
    }
}
