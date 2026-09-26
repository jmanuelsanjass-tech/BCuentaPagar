// src/main/java/com/ejemplo/proveedor/service/ProveedorService.java
package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.model.Proveedor;
import com.ejemplo.proveedor.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

  @Autowired
  private ProveedorRepository repository;

  @Transactional(readOnly = true)
  public List<Proveedor> listar() {
    return repository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<Proveedor> obtener(Long id) {
    return repository.findById(id);
  }

  @Transactional
  public Proveedor guardar(Proveedor proveedor) {
    return repository.save(proveedor);
  }

  @Transactional
  public void eliminar(Long id) {
    repository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public boolean existePorRuc(String ruc) {
    return repository.existsByRuc(ruc);
  }
}