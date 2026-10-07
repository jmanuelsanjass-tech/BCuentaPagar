package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
