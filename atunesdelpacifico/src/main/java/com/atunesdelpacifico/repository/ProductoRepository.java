package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByConservante(Producto.TipoConservante conservante);

    Optional<Producto> findByCodigoSku(String codigoSku);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Producto> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Producto> findByNombreOrDescripcionContainingIgnoreCase(@Param("texto") String texto);

    Boolean existsByCodigoSku(String codigoSku);

    @Query("SELECT COUNT(p) FROM Producto p")
    Long countTotalProductos();
}
