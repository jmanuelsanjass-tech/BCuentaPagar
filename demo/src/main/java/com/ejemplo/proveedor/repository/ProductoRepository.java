package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.Producto;
import com.ejemplo.proveedor.dto.ProductoBusquedaResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

  Optional<Producto> findByCodigoBarras(String codigoBarras);

  List<Producto> findByNombreContainingIgnoreCase(String nombre);

  List<Producto> findByCategoriaIgnoreCase(String categoria);

  @Query("""
      select new com.ejemplo.proveedor.dto.ProductoBusquedaResponse(
          p.idProducto, p.codigoBarras, p.nombre, p.descripcion, p.categoria, p.precio, p.stock)
      from Producto p
      where lower(p.estado) = 'activo'
        and (
          lower(p.nombre) like lower(concat('%', :nombre, '%'))
          or lower(p.codigoBarras) like lower(concat('%', :nombre, '%'))
        )
      order by p.nombre asc
      """)
  List<ProductoBusquedaResponse> buscarPredictivo(
      @Param("nombre") String nombre, Pageable pageable);

  boolean existsByCodigoBarras(String codigoBarras);
}
