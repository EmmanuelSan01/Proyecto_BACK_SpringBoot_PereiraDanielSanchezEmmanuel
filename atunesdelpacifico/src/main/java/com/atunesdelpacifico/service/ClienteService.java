package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.Cliente;
import com.atunesdelpacifico.entity.Rol;
import com.atunesdelpacifico.entity.Usuario;
import com.atunesdelpacifico.model.dto.ClienteRequest;
import com.atunesdelpacifico.repository.ClienteRepository;
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
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> findByUsuarioNombre(String nombreUsuario) {
        return clienteRepository.findByUsuarioNombreUsuario(nombreUsuario);
    }

    public List<Cliente> findByEstado(Cliente.EstadoCliente estado) {
        return clienteRepository.findByEstado(estado);
    }

    public List<Cliente> findByTipoCliente(Cliente.TipoCliente tipo) {
        return clienteRepository.findByTipo(tipo);
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Cliente registrarCliente(ClienteRequest clienteRequest) {
        // Verificar que no exista el usuario o correo
        if (usuarioRepository.existsByNombreUsuario(clienteRequest.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        if (usuarioRepository.existsByCorreo(clienteRequest.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        if (clienteRepository.existsByIdentificacion(clienteRequest.getIdentificacion())) {
            throw new RuntimeException("La identificación ya está registrada");
        }

        // Obtener rol de cliente
        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(clienteRequest.getNombreUsuario());
        usuario.setContrasena(passwordEncoder.encode(clienteRequest.getContrasena()));
        usuario.setCorreo(clienteRequest.getCorreo());
        usuario.setRol(rolCliente);
        usuario.setActivo(true);

        usuario = usuarioRepository.save(usuario);

        // Crear cliente
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setTipo(clienteRequest.getTipo());
        cliente.setNombre(clienteRequest.getNombre());
        cliente.setIdentificacion(clienteRequest.getIdentificacion());
        cliente.setTelefono(clienteRequest.getTelefono());
        cliente.setDireccion(clienteRequest.getDireccion());
        cliente.setEstado(Cliente.EstadoCliente.ACTIVO);

        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, ClienteRequest clienteRequest) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Actualizar datos del cliente
        cliente.setTipo(clienteRequest.getTipo());
        cliente.setNombre(clienteRequest.getNombre());
        cliente.setTelefono(clienteRequest.getTelefono());
        cliente.setDireccion(clienteRequest.getDireccion());

        // Actualizar usuario si es necesario
        Usuario usuario = cliente.getUsuario();
        if (!usuario.getCorreo().equals(clienteRequest.getCorreo())) {
            if (usuarioRepository.existsByCorreo(clienteRequest.getCorreo())) {
                throw new RuntimeException("El correo ya está registrado");
            }
            usuario.setCorreo(clienteRequest.getCorreo());
        }

        if (clienteRequest.getContrasena() != null && !clienteRequest.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(clienteRequest.getContrasena()));
        }

        usuarioRepository.save(usuario);
        return clienteRepository.save(cliente);
    }

    public void deleteById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Cambiar estado a inactivo en lugar de eliminar
        cliente.setEstado(Cliente.EstadoCliente.INACTIVO);
        cliente.getUsuario().setActivo(false);

        usuarioRepository.save(cliente.getUsuario());
        clienteRepository.save(cliente);
    }

    public Cliente cambiarEstado(Long id, Cliente.EstadoCliente estado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setEstado(estado);

        // Sincronizar con el estado del usuario
        cliente.getUsuario().setActivo(estado == Cliente.EstadoCliente.ACTIVO);
        usuarioRepository.save(cliente.getUsuario());

        return clienteRepository.save(cliente);
    }

    public boolean isOwner(Long clienteId, String nombreUsuario) {
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        return cliente.isPresent() &&
                cliente.get().getUsuario().getNombreUsuario().equals(nombreUsuario);
    }
}
