package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.Rol;
import com.atunesdelpacifico.entity.Usuario;
import com.atunesdelpacifico.model.dto.UsuarioRequest;
import com.atunesdelpacifico.repository.RolRepository;
import com.atunesdelpacifico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> findAll() {
        try {
            System.out.println("=== UsuarioService.findAll() iniciado ===");
            List<Usuario> usuarios = usuarioRepository.findAll();
            System.out.println("=== Usuarios encontrados: " + usuarios.size() + " ===");

            // Log de cada usuario para debug
            for (Usuario usuario : usuarios) {
                System.out.println("Usuario: " + usuario.getNombreUsuario() +
                        ", Rol: " + (usuario.getRol() != null ? usuario.getRol().getNombre() : "NULL"));
            }

            return usuarios;
        } catch (Exception e) {
            System.err.println("=== Error en UsuarioService.findAll(): " + e.getMessage() + " ===");
            e.printStackTrace();
            throw new RuntimeException("Error al obtener usuarios: " + e.getMessage(), e);
        }
    }

    public Optional<Usuario> findById(Long id) {
        try {
            return usuarioRepository.findById(id);
        } catch (Exception e) {
            System.err.println("Error al buscar usuario por ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Error al buscar usuario: " + e.getMessage(), e);
        }
    }

    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        try {
            return usuarioRepository.findByNombreUsuario(nombreUsuario);
        } catch (Exception e) {
            System.err.println("Error al buscar usuario por nombre: " + e.getMessage());
            throw new RuntimeException("Error al buscar usuario: " + e.getMessage(), e);
        }
    }

    public Optional<Usuario> findByCorreo(String correo) {
        try {
            return usuarioRepository.findByCorreo(correo);
        } catch (Exception e) {
            System.err.println("Error al buscar usuario por correo: " + e.getMessage());
            throw new RuntimeException("Error al buscar usuario: " + e.getMessage(), e);
        }
    }

    public List<Usuario> findByRol(String rolNombre) {
        try {
            return usuarioRepository.findByRolNombre(rolNombre);
        } catch (Exception e) {
            System.err.println("Error al buscar usuarios por rol: " + e.getMessage());
            throw new RuntimeException("Error al buscar usuarios por rol: " + e.getMessage(), e);
        }
    }

    public Usuario save(UsuarioRequest usuarioRequest) {
        try {
            // Verificar que no exista el usuario o correo
            if (usuarioRepository.existsByNombreUsuario(usuarioRequest.getNombreUsuario())) {
                throw new RuntimeException("El nombre de usuario ya existe");
            }
            if (usuarioRepository.existsByCorreo(usuarioRequest.getCorreo())) {
                throw new RuntimeException("El correo ya está registrado");
            }

            // Obtener el rol
            Rol rol = rolRepository.findById(usuarioRequest.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            Usuario usuario = new Usuario();
            usuario.setNombreUsuario(usuarioRequest.getNombreUsuario());
            usuario.setContrasena(passwordEncoder.encode(usuarioRequest.getContrasena()));
            usuario.setCorreo(usuarioRequest.getCorreo());
            usuario.setRol(rol);
            usuario.setActivo(usuarioRequest.getActivo() != null ? usuarioRequest.getActivo() : true);

            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            throw new RuntimeException("Error al crear usuario: " + e.getMessage(), e);
        }
    }

    public Usuario update(Long id, UsuarioRequest usuarioRequest) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar nombre de usuario si cambió
            if (!usuario.getNombreUsuario().equals(usuarioRequest.getNombreUsuario())) {
                if (usuarioRepository.existsByNombreUsuario(usuarioRequest.getNombreUsuario())) {
                    throw new RuntimeException("El nombre de usuario ya existe");
                }
                usuario.setNombreUsuario(usuarioRequest.getNombreUsuario());
            }

            // Verificar correo si cambió
            if (!usuario.getCorreo().equals(usuarioRequest.getCorreo())) {
                if (usuarioRepository.existsByCorreo(usuarioRequest.getCorreo())) {
                    throw new RuntimeException("El correo ya está registrado");
                }
                usuario.setCorreo(usuarioRequest.getCorreo());
            }

            // Actualizar contraseña si se proporciona
            if (usuarioRequest.getContrasena() != null && !usuarioRequest.getContrasena().isEmpty()) {
                usuario.setContrasena(passwordEncoder.encode(usuarioRequest.getContrasena()));
            }

            // Actualizar rol
            Rol rol = rolRepository.findById(usuarioRequest.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            usuario.setRol(rol);
            usuario.setActivo(usuarioRequest.getActivo() != null ? usuarioRequest.getActivo() : true);

            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Cambiar estado a inactivo en lugar de eliminar
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }

    public boolean existsByNombreUsuario(String nombreUsuario) {
        try {
            return usuarioRepository.existsByNombreUsuario(nombreUsuario);
        } catch (Exception e) {
            System.err.println("Error al verificar existencia de usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean existsByCorreo(String correo) {
        try {
            return usuarioRepository.existsByCorreo(correo);
        } catch (Exception e) {
            System.err.println("Error al verificar existencia de correo: " + e.getMessage());
            return false;
        }
    }
}
