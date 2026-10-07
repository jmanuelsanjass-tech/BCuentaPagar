// src/main/java/com/ejemplo/proveedor/service/ProveedorService.java
package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.model.Proveedor;
import com.ejemplo.proveedor.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

  private final ProveedorRepository repository;

  public ProveedorService(ProveedorRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<Proveedor> listar() {
    return repository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<Proveedor> obtener(String clvprv) {
    return repository.findById(clvprv);
  }

  @Transactional
  public Proveedor guardar(Proveedor proveedor) {
    return repository.save(proveedor);
  }

  @Transactional
  public boolean actualizar(String clvprv, Proveedor datos) {
    return repository.findById(clvprv).map(proveedor -> {
      proveedor.setNomprv(datos.getNomprv());
      proveedor.setTipprv(datos.getTipprv());
      proveedor.setRfcprv(datos.getRfcprv());
      proveedor.setTelprv(datos.getTelprv());
      proveedor.setCorrprv(datos.getCorrprv());
      proveedor.setDirprv(datos.getDirprv());
      proveedor.setCodpos(datos.getCodpos());
      proveedor.setEstprv(datos.getEstprv());
      proveedor.setClabe(datos.getClabe());
      repository.save(proveedor);
      return true;
    }).orElse(false);
  }

  @Transactional(readOnly = true)
  public boolean existePorRfcprv(String rfcprv) {
    return repository.existsByRfcprv(rfcprv);
  }

  @Transactional
  public boolean eliminar(String clvprv) {
    return repository.findById(clvprv).map(proveedor -> {
      repository.delete(proveedor);
      return true;
    }).orElse(false);
  }
}