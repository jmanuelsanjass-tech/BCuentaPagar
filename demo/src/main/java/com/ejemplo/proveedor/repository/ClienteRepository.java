package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
    List<Cliente> findByNombreContainingIgnoreCaseOrIdCliente(String nombre, Long idCliente);
}