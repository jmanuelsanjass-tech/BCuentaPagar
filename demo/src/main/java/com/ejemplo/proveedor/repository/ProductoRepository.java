package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

  Optional<Producto> findByCodigoBarras(String codigoBarras);

  List<Producto> findByNombreContainingIgnoreCase(String nombre);

  List<Producto> findByCategoriaIgnoreCase(String categoria);

  boolean existsByCodigoBarras(String codigoBarras);
}
