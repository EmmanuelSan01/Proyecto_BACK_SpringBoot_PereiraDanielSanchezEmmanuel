package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.model.dto.JwtResponse;
import com.atunesdelpacifico.model.dto.LoginRequest;
import com.atunesdelpacifico.security.JwtTokenProvider;
import com.atunesdelpacifico.security.UserPrincipal;
import com.atunesdelpacifico.service.UsuarioService;
import com.atunesdelpacifico.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.atunesdelpacifico.entity.Cliente;
import com.atunesdelpacifico.model.dto.ClienteRequest;
import com.atunesdelpacifico.service.ClienteService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación de usuarios")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("=== INICIO LOGIN ===");
            System.out.println("Usuario: " + loginRequest.getNombreUsuario());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getNombreUsuario(),
                            loginRequest.getContrasena()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // Buscar el usuario completo para obtener el rol
            Usuario usuario = usuarioService.findByNombreUsuario(loginRequest.getNombreUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            System.out.println("Usuario encontrado: " + usuario.getNombreUsuario());
            System.out.println("Rol del usuario: " + usuario.getRol().getNombre());

            // Crear respuesta directa (sin ApiResponse wrapper)
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", jwt);
            response.put("nombreUsuario", usuario.getNombreUsuario());
            response.put("correo", usuario.getCorreo());
            response.put("rol", usuario.getRol().getNombre());

            System.out.println("Respuesta enviada: " + response);
            System.out.println("=== FIN LOGIN ===");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error en login: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Credenciales inválidas: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión del usuario actual")
    public ResponseEntity<ApiResponse<String>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logout exitoso", null));
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo cliente", description = "Registra un nuevo cliente en el sistema")
    public ResponseEntity<ApiResponse<Cliente>> registrarCliente(@Valid @RequestBody ClienteRequest clienteRequest) {
        try {
            System.out.println("=== INICIO REGISTRO ===");
            System.out.println("Datos recibidos: " + clienteRequest);

            Cliente cliente = clienteService.registrarCliente(clienteRequest);

            System.out.println("Cliente registrado: " + cliente.getNombre());
            System.out.println("=== FIN REGISTRO ===");

            return ResponseEntity.ok(ApiResponse.success("Cliente registrado exitosamente", cliente));
        } catch (Exception e) {
            System.out.println("Error en registro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al registrar cliente: " + e.getMessage()));
        }
    }
}
