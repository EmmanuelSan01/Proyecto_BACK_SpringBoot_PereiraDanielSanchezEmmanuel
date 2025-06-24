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
        try {
            System.out.println("=== ClienteService.findAll() iniciado ===");
            List<Cliente> clientes = clienteRepository.findAll();
            System.out.println("=== Clientes encontrados: " + clientes.size() + " ===");

            // Log de cada cliente para debug
            for (Cliente cliente : clientes) {
                System.out.println("Cliente: " + cliente.getNombre() +
                        ", Estado: " + cliente.getEstado() +
                        ", Usuario: " + (cliente.getUsuario() != null ? cliente.getUsuario().getNombreUsuario() : "NULL"));
            }

            return clientes;
        } catch (Exception e) {
            System.err.println("=== Error en ClienteService.findAll(): " + e.getMessage() + " ===");
            e.printStackTrace();
            throw new RuntimeException("Error al obtener clientes: " + e.getMessage(), e);
        }
    }

    public Optional<Cliente> findById(Long id) {
        try {
            return clienteRepository.findById(id);
        } catch (Exception e) {
            System.err.println("Error al buscar cliente por ID: " + e.getMessage());
            throw new RuntimeException("Error al buscar cliente: " + e.getMessage(), e);
        }
    }

    public Optional<Cliente> findByUsuarioNombre(String nombreUsuario) {
        try {
            return clienteRepository.findByUsuarioNombreUsuario(nombreUsuario);
        } catch (Exception e) {
            System.err.println("Error al buscar cliente por usuario: " + e.getMessage());
            throw new RuntimeException("Error al buscar cliente: " + e.getMessage(), e);
        }
    }

    public List<Cliente> findByEstado(Cliente.EstadoCliente estado) {
        try {
            return clienteRepository.findByEstado(estado);
        } catch (Exception e) {
            System.err.println("Error al buscar clientes por estado: " + e.getMessage());
            throw new RuntimeException("Error al buscar clientes: " + e.getMessage(), e);
        }
    }

    public List<Cliente> findByTipoCliente(Cliente.TipoCliente tipo) {
        try {
            return clienteRepository.findByTipo(tipo);
        } catch (Exception e) {
            System.err.println("Error al buscar clientes por tipo: " + e.getMessage());
            throw new RuntimeException("Error al buscar clientes: " + e.getMessage(), e);
        }
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        try {
            return clienteRepository.findByNombreContainingIgnoreCase(nombre);
        } catch (Exception e) {
            System.err.println("Error al buscar clientes por nombre: " + e.getMessage());
            throw new RuntimeException("Error al buscar clientes: " + e.getMessage(), e);
        }
    }

    public Cliente registrarCliente(ClienteRequest clienteRequest) {
        try {
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

            // Convertir String a enum
            Cliente.TipoCliente tipoEnum;
            try {
                tipoEnum = Cliente.TipoCliente.valueOf(clienteRequest.getTipo());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Tipo de cliente inválido: " + clienteRequest.getTipo());
            }

            cliente.setTipo(tipoEnum);
            cliente.setNombre(clienteRequest.getNombre());
            cliente.setIdentificacion(clienteRequest.getIdentificacion());
            cliente.setTelefono(clienteRequest.getTelefono());
            cliente.setDireccion(clienteRequest.getDireccion());
            cliente.setEstado(Cliente.EstadoCliente.ACTIVO);

            return clienteRepository.save(cliente);
        } catch (Exception e) {
            System.err.println("Error al registrar cliente: " + e.getMessage());
            throw new RuntimeException("Error al registrar cliente: " + e.getMessage(), e);
        }
    }

    public Cliente update(Long id, ClienteRequest clienteRequest) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            // Actualizar datos del cliente
            // Convertir String a enum
            Cliente.TipoCliente tipoEnum;
            try {
                tipoEnum = Cliente.TipoCliente.valueOf(clienteRequest.getTipo());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Tipo de cliente inválido: " + clienteRequest.getTipo());
            }
            cliente.setTipo(tipoEnum);
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
        } catch (Exception e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            throw new RuntimeException("Error al actualizar cliente: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            // Cambiar estado a inactivo en lugar de eliminar
            cliente.setEstado(Cliente.EstadoCliente.INACTIVO);
            cliente.getUsuario().setActivo(false);

            usuarioRepository.save(cliente.getUsuario());
            clienteRepository.save(cliente);
        } catch (Exception e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            throw new RuntimeException("Error al eliminar cliente: " + e.getMessage(), e);
        }
    }

    public Cliente cambiarEstado(Long id, Cliente.EstadoCliente estado) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            cliente.setEstado(estado);

            // Sincronizar con el estado del usuario
            cliente.getUsuario().setActivo(estado == Cliente.EstadoCliente.ACTIVO);
            usuarioRepository.save(cliente.getUsuario());

            return clienteRepository.save(cliente);
        } catch (Exception e) {
            System.err.println("Error al cambiar estado del cliente: " + e.getMessage());
            throw new RuntimeException("Error al cambiar estado: " + e.getMessage(), e);
        }
    }

    public boolean isOwner(Long clienteId, String nombreUsuario) {
        try {
            Optional<Cliente> cliente = clienteRepository.findById(clienteId);
            return cliente.isPresent() &&
                    cliente.get().getUsuario().getNombreUsuario().equals(nombreUsuario);
        } catch (Exception e) {
            System.err.println("Error al verificar propietario: " + e.getMessage());
            return false;
        }
    }
}
