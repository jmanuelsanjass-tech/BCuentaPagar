package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, String> {

  @Override
  @EntityGraph(attributePaths = "inventario")
  List<Producto> findAll();

  @Override
  @EntityGraph(attributePaths = "inventario")
  Optional<Producto> findById(String codigoBarras);

  @EntityGraph(attributePaths = "inventario")
  List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
