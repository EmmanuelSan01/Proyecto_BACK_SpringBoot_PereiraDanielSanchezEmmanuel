package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByNombreUsuarioOrCorreo(String nombreUsuario, String correo);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.nombreUsuario = :nombreUsuario")
    Optional<Usuario> findByNombreUsuarioWithRol(@Param("nombreUsuario") String nombreUsuario);

    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = :rolNombre")
    List<Usuario> findByRolNombre(@Param("rolNombre") String rolNombre);

    List<Usuario> findByActivo(Boolean activo);

    Boolean existsByNombreUsuario(String nombreUsuario);

    Boolean existsByCorreo(String correo);
}
