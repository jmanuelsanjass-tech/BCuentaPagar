package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.Pago;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, UUID> {

  boolean existsByEmpresaIdAndNumeroPago(UUID empresaId, String numeroPago);

  boolean existsByEmpresaIdAndNumeroPagoAndIdNot(UUID empresaId, String numeroPago, UUID id);

  boolean existsByFactura_Id(UUID facturaId);
}
