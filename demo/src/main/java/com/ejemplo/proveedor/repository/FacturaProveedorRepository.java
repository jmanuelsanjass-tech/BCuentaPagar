package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.FacturaProveedor;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacturaProveedorRepository extends JpaRepository<FacturaProveedor, UUID> {

  boolean existsByEmpresaIdAndProveedor_IdProveedorAndNumeroFactura(
      UUID empresaId, Long proveedorId, String numeroFactura);

  boolean existsByEmpresaIdAndProveedor_IdProveedorAndNumeroFacturaAndIdNot(
      UUID empresaId, Long proveedorId, String numeroFactura, UUID id);

  boolean existsByRecepcion_Id(UUID recepcionId);

  boolean existsByOrdenCompra_IdOrdenCompra(UUID ordenCompraId);

  @Query("""
      select coalesce(sum(p.monto), 0)
      from Pago p
      where p.factura.id = :facturaId and p.estado = 'CONFIRMADO'
      """)
  BigDecimal sumarPagosConfirmados(@Param("facturaId") UUID facturaId);
}
