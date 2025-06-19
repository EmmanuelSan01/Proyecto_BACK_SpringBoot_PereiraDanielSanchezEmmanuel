package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.entity.Usuario;
import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.model.dto.UsuarioRequest;
import com.atunesdelpacifico.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene la lista completa de usuarios")
    public ResponseEntity<ApiResponse<List<Usuario>>> getAllUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Usuarios obtenidos exitosamente", usuarios));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener usuarios: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene un usuario específico por su ID")
    public ResponseEntity<ApiResponse<Usuario>> getUsuarioById(@PathVariable Long id) {
        try {
            Optional<Usuario> usuario = usuarioService.findById(id);
            if (usuario.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Usuario encontrado", usuario.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener usuario: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    public ResponseEntity<ApiResponse<Usuario>> createUsuario(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        try {
            Usuario usuario = usuarioService.save(usuarioRequest);
            return ResponseEntity.ok(ApiResponse.success("Usuario creado exitosamente", usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al crear usuario: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    public ResponseEntity<ApiResponse<Usuario>> updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequest usuarioRequest) {
        try {
            Usuario usuario = usuarioService.update(id, usuarioRequest);
            return ResponseEntity.ok(ApiResponse.success("Usuario actualizado exitosamente", usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al actualizar usuario: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    public ResponseEntity<ApiResponse<String>> deleteUsuario(@PathVariable Long id) {
        try {
            usuarioService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Usuario eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al eliminar usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/rol/{rolNombre}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Buscar usuarios por rol", description = "Obtiene usuarios filtrados por rol")
    public ResponseEntity<ApiResponse<List<Usuario>>> getUsuariosByRol(@PathVariable String rolNombre) {
        try {
            List<Usuario> usuarios = usuarioService.findByRol(rolNombre);
            return ResponseEntity.ok(ApiResponse.success("Usuarios obtenidos exitosamente", usuarios));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener usuarios: " + e.getMessage()));
        }
    }
}
