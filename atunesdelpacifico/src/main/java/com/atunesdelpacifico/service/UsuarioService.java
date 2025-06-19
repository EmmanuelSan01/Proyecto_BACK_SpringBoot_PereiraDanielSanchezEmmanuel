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
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> findByRol(String rolNombre) {
        return usuarioRepository.findByRolNombre(rolNombre);
    }

    public Usuario save(UsuarioRequest usuarioRequest) {
        // Verificar que no exista el usuario o correo
        if (usuarioRepository.existsByNombreUsuario(usuarioRequest.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        if (usuarioRepository.existsByCorreo(usuarioRequest.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Obtener el rol - convertir Byte a Long
        Long rolId = usuarioRequest.getRolId().longValue();
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(usuarioRequest.getNombreUsuario());
        usuario.setContrasena(passwordEncoder.encode(usuarioRequest.getContrasena()));
        usuario.setCorreo(usuarioRequest.getCorreo());
        usuario.setRol(rol);
        usuario.setActivo(usuarioRequest.getActivo());

        return usuarioRepository.save(usuario);
    }

    public Usuario update(Long id, UsuarioRequest usuarioRequest) {
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

        // Actualizar rol - convertir Byte a Long
        Long rolId = usuarioRequest.getRolId().longValue();
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rol);
        usuario.setActivo(usuarioRequest.getActivo());

        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Cambiar estado a inactivo en lugar de eliminar
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}
