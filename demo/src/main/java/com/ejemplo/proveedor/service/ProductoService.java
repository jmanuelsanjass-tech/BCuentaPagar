package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.model.Producto;
import com.ejemplo.proveedor.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

  private final ProductoRepository repository;

  public ProductoService(ProductoRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<Producto> listar() {
    return repository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Producto> buscarPorNombre(String nombre) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El término de búsqueda no puede estar vacío");
    }
    return repository.findByNombreContainingIgnoreCase(nombre.trim());
  }

  @Transactional(readOnly = true)
  public Optional<Producto> obtener(String codigoBarras) {
    return repository.findById(codigoBarras);
  }

  @Transactional
  public Producto crear(Producto producto) {
    producto.setInventario(null);
    Producto guardado = repository.saveAndFlush(producto);
    return cargarConInventario(guardado.getCodigoBarras());
  }

  @Transactional
  public Optional<Producto> actualizar(String codigoBarras, Producto datos) {
    return repository.findById(codigoBarras).map(producto -> {
      producto.setModelo(datos.getModelo());
      producto.setNombre(datos.getNombre());
      producto.setCosto(datos.getCosto());
      repository.saveAndFlush(producto);
      return cargarConInventario(codigoBarras);
    });
  }

  @Transactional
  public boolean eliminar(String codigoBarras) {
    return repository.findById(codigoBarras).map(producto -> {
      repository.delete(producto);
      return true;
    }).orElse(false);
  }

  private Producto cargarConInventario(String codigoBarras) {
    return repository.findById(codigoBarras)
        .orElseThrow(() -> new IllegalStateException(
            "No se pudo volver a cargar el producto guardado: " + codigoBarras));
  }
}
