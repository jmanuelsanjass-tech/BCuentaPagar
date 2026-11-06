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
  public Optional<Proveedor> obtener(Long idProveedor) {
    return repository.findById(idProveedor);
  }

  @Transactional
  public Proveedor guardar(Proveedor proveedor) {
    return repository.save(proveedor);
  }

  @Transactional
  public boolean actualizar(Long idProveedor, Proveedor datos) {
    return repository.findById(idProveedor).map(proveedor -> {
      proveedor.setNombre(datos.getNombre());
      proveedor.setTelefono(datos.getTelefono());
      proveedor.setCorreo(datos.getCorreo());
      proveedor.setDireccion(datos.getDireccion());
      proveedor.setEstadoDireccion(datos.getEstadoDireccion());
      proveedor.setEstado(datos.getEstado());
      proveedor.setProductosSuministrados(datos.getProductosSuministrados());
      proveedor.setUsuarioAuditoria(datos.getUsuarioAuditoria());
      proveedor.setQrCode(datos.getQrCode());
      repository.save(proveedor);
      return true;
    }).orElse(false);
  }

  @Transactional(readOnly = true)
  public boolean existePorCorreo(String correo) {
    return repository.existsByCorreo(correo);
  }

  @Transactional
  public boolean eliminar(Long idProveedor) {
    return repository.findById(idProveedor).map(proveedor -> {
      repository.delete(proveedor);
      return true;
    }).orElse(false);
  }
}