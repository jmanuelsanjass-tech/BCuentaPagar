package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.RecepcionMercanciaLinea;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecepcionMercanciaLineaRepository
    extends JpaRepository<RecepcionMercanciaLinea, UUID> {

  List<RecepcionMercanciaLinea> findAllByRecepcion_IdOrderByIdAsc(UUID recepcionId);
}
