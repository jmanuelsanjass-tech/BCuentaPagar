package com.ejemplo.proveedor.repository;
import com.ejemplo.proveedor.model.Proveedor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProveedorRepository
        extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Proveedor> findTop10ByNombreContainingIgnoreCase(
            String nombre);
}

