package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.Cliente;
import com.atunesdelpacifico.entity.Usuario;
import com.atunesdelpacifico.model.dto.ClienteRequest;
import com.atunesdelpacifico.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UsuarioService usuarioService;
    
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }
    
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }
    
    public List<Cliente> findByEstado(Cliente.EstadoCliente estado) {
        return clienteRepository.findByEstado(estado);
    }
    
    public Cliente save(ClienteRequest clienteRequest) {
        // Verificar si la identificación ya existe
        if (clienteRepository.existsByIdentificacion(clienteRequest.getIdentificacion())) {
            throw new RuntimeException("La identificación ya está registrada");
        }
        
        // Crear el usuario primero
        Usuario usuario = usuarioService.save(clienteRequest.getUsuario());
        
        // Crear el cliente
        Cliente cliente = new Cliente();
        cliente.setTipo(clienteRequest.getTipo());
        cliente.setNombre(clienteRequest.getNombre());
        cliente.setIdentificacion(clienteRequest.getIdentificacion());
        cliente.setTelefono(clienteRequest.getTelefono());
        cliente.setDireccion(clienteRequest.getDireccion());
        cliente.setUsuario(usuario);
        cliente.setIdUsuario(usuario.getIdUsuario());
        
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
        
        // Actualizar usuario si se proporciona
        if (clienteRequest.getUsuario() != null) {
            usuarioService.update(cliente.getIdUsuario(), clienteRequest.getUsuario());
        }
        
        return clienteRepository.save(cliente);
    }
    
    public void deleteById(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado");
        }
        clienteRepository.deleteById(id);
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
}
