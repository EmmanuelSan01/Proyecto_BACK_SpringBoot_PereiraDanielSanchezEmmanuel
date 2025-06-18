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

    public List<Cliente> findByEstado(Cliente.EstadoCliente estado) {
        return clienteRepository.findByEstado(estado);
    }

    public List<Cliente> findByTipoCliente(Cliente.TipoCliente tipoCliente) {
        return clienteRepository.findByTipo(tipoCliente);
    }

    public Cliente registrarCliente(ClienteRequest clienteRequest) {
        // Verificar que el correo no esté en uso
        if (usuarioRepository.existsByCorreo(clienteRequest.getUsuario().getCorreo())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        if (usuarioRepository.existsByNombreUsuario(clienteRequest.getUsuario().getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // Verificar que la identificación no esté en uso
        if (clienteRepository.existsByIdentificacion(clienteRequest.getIdentificacion())) {
            throw new RuntimeException("La identificación ya está registrada");
        }

        // Obtener el rol (por defecto Cliente = 1)
        Rol rol = rolRepository.findById((byte) 1)
                .orElseThrow(() -> new RuntimeException("Rol Cliente no encontrado"));

        // Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(clienteRequest.getUsuario().getNombreUsuario());
        usuario.setContrasena(passwordEncoder.encode(clienteRequest.getUsuario().getContrasena()));
        usuario.setCorreo(clienteRequest.getUsuario().getCorreo());
        usuario.setRol(rol);

        usuario = usuarioRepository.save(usuario);

        // Crear el cliente
        Cliente cliente = new Cliente();
        cliente.setIdUsuario(usuario.getIdUsuario());
        cliente.setTipo(clienteRequest.getTipo());
        cliente.setNombre(clienteRequest.getNombre());
        cliente.setIdentificacion(clienteRequest.getIdentificacion());
        cliente.setTelefono(clienteRequest.getTelefono());
        cliente.setDireccion(clienteRequest.getDireccion());
        cliente.setEstado(Cliente.EstadoCliente.ACTIVO);
        cliente.setUsuario(usuario);

        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, ClienteRequest clienteRequest) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Verificar duplicados (excluyendo el cliente actual)
        if (!cliente.getIdentificacion().equals(clienteRequest.getIdentificacion()) &&
                clienteRepository.existsByIdentificacion(clienteRequest.getIdentificacion())) {
            throw new RuntimeException("La identificación ya está registrada");
        }

        // Actualizar datos del cliente
        cliente.setTipo(clienteRequest.getTipo());
        cliente.setNombre(clienteRequest.getNombre());
        cliente.setIdentificacion(clienteRequest.getIdentificacion());
        cliente.setTelefono(clienteRequest.getTelefono());
        cliente.setDireccion(clienteRequest.getDireccion());

        return clienteRepository.save(cliente);
    }

    public void deleteById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Cambiar estado a inactivo en lugar de eliminar
        cliente.setEstado(Cliente.EstadoCliente.INACTIVO);
        clienteRepository.save(cliente);
    }

    public Cliente cambiarEstado(Long id, Cliente.EstadoCliente nuevoEstado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setEstado(nuevoEstado);
        return clienteRepository.save(cliente);
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Optional<Cliente> findByUsuarioNombre(String nombreUsuario) {
        return clienteRepository.findByUsuarioNombreUsuario(nombreUsuario);
    }

    public boolean isOwner(Long clienteId, String nombreUsuario) {
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        return cliente.isPresent() &&
                cliente.get().getUsuario().getNombreUsuario().equals(nombreUsuario);
    }
}
