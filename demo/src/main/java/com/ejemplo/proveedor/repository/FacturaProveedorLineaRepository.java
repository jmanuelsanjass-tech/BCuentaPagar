package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.FacturaProveedorLinea;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacturaProveedorLineaRepository
    extends JpaRepository<FacturaProveedorLinea, UUID> {

  List<FacturaProveedorLinea> findAllByFactura_IdOrderByNumeroLineaAsc(UUID facturaId);

  Optional<FacturaProveedorLinea> findByFactura_IdAndId(UUID facturaId, UUID id);

  boolean existsByFactura_IdAndNumeroLinea(UUID facturaId, Integer numeroLinea);

  boolean existsByFactura_IdAndNumeroLineaAndIdNot(
      UUID facturaId, Integer numeroLinea, UUID id);

  boolean existsByOrdenCompraLineaId(UUID ordenCompraLineaId);

  @Query(value = """
      SELECT EXISTS (
          SELECT 1
          FROM public.factura_proveedor_linea fpl
          JOIN public.orden_compra_linea ocl ON ocl.id = fpl.orden_compra_linea_id
          WHERE ocl.orden_compra_id = :ordenCompraId
      )
      """, nativeQuery = true)
  boolean existsReferencingOrdenCompra(@Param("ordenCompraId") UUID ordenCompraId);
}
