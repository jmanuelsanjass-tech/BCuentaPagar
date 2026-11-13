package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.PoliticaAprobacion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticaAprobacionRepository extends JpaRepository<PoliticaAprobacion, UUID> {
}
