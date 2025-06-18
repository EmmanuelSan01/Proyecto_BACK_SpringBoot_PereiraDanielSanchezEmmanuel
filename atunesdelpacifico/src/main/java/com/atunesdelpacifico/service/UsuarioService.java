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
    
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }
    
    public Usuario save(UsuarioRequest usuarioRequest) {
        // Verificar si el usuario ya existe
        if (usuarioRepository.existsByNombreUsuario(usuarioRequest.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        
        if (usuarioRepository.existsByCorreo(usuarioRequest.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        // Buscar el rol
        Rol rol = rolRepository.findByNombre(usuarioRequest.getRolNombre())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + usuarioRequest.getRolNombre()));
        
        // Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(usuarioRequest.getNombreUsuario());
        usuario.setContrasena(passwordEncoder.encode(usuarioRequest.getContrasena()));
        usuario.setCorreo(usuarioRequest.getCorreo());
        usuario.setRol(rol);
        
        return usuarioRepository.save(usuario);
    }
    
    public Usuario update(Long id, UsuarioRequest usuarioRequest) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar duplicados (excluyendo el usuario actual)
        if (!usuario.getNombreUsuario().equals(usuarioRequest.getNombreUsuario()) &&
            usuarioRepository.existsByNombreUsuario(usuarioRequest.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        
        if (!usuario.getCorreo().equals(usuarioRequest.getCorreo()) &&
            usuarioRepository.existsByCorreo(usuarioRequest.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        // Actualizar datos
        usuario.setNombreUsuario(usuarioRequest.getNombreUsuario());
        usuario.setCorreo(usuarioRequest.getCorreo());
        
        if (usuarioRequest.getContrasena() != null && !usuarioRequest.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(usuarioRequest.getContrasena()));
        }
        
        if (usuarioRequest.getRolNombre() != null) {
            Rol rol = rolRepository.findByNombre(usuarioRequest.getRolNombre())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + usuarioRequest.getRolNombre()));
            usuario.setRol(rol);
        }
        
        return usuarioRepository.save(usuario);
    }
    
    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }
    
    public List<Usuario> findByRol(String rolNombre) {
        return usuarioRepository.findByRolNombre(rolNombre);
    }
}
