package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.model.Producto;
import com.ejemplo.proveedor.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    return repository.findByNombreContainingIgnoreCase(nombre.trim());
  }

  @Transactional(readOnly = true)
  public List<Producto> buscarPorCategoria(String categoria) {
    return repository.findByCategoriaIgnoreCase(categoria.trim());
  }

  @Transactional(readOnly = true)
  public Optional<Producto> obtener(Long idProducto) {
    return repository.findById(idProducto);
  }

  @Transactional(readOnly = true)
  public Optional<Producto> obtenerPorCodigoBarras(String codigoBarras) {
    return repository.findByCodigoBarras(codigoBarras);
  }

  @Transactional
  public Producto crear(Producto datos) {
    Producto producto = new Producto();
    aplicarDatos(producto, datos);
    return repository.saveAndFlush(producto);
  }

  @Transactional
  public Optional<Producto> actualizar(Long idProducto, Producto datos) {
    return repository.findById(idProducto).map(producto -> {
      aplicarDatos(producto, datos);
      return repository.saveAndFlush(producto);
    });
  }

  @Transactional
  public Optional<Producto> actualizarPorCodigoBarras(String codigoBarras, Producto datos) {
    return repository.findByCodigoBarras(codigoBarras).map(producto -> {
      aplicarDatos(producto, datos);
      return repository.saveAndFlush(producto);
    });
  }

  @Transactional
  public boolean eliminar(Long idProducto) {
    return repository.findById(idProducto).map(producto -> {
      repository.delete(producto);
      return true;
    }).orElse(false);
  }

  @Transactional
  public boolean eliminarPorCodigoBarras(String codigoBarras) {
    return repository.findByCodigoBarras(codigoBarras).map(producto -> {
      repository.delete(producto);
      return true;
    }).orElse(false);
  }

  private void aplicarDatos(Producto producto, Producto datos) {
    producto.setNombre(datos.getNombre());
    producto.setDescripcion(datos.getDescripcion());
    producto.setCategoria(datos.getCategoria() == null ? "Abarrotes" : datos.getCategoria());
    producto.setPrecio(datos.getPrecio());
    producto.setStock(datos.getStock() == null ? 0 : datos.getStock());
    producto.setEstado(datos.getEstado() == null ? "activo" : datos.getEstado());
    producto.setCodigoBarras(datos.getCodigoBarras());
    producto.setUsuarioAuditoria(usuarioAutenticado());
    producto.setQrCode(datos.getQrCode());
  }

  private String usuarioAutenticado() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()
        || "anonymousUser".equals(authentication.getName())) {
      return null;
    }
    return authentication.getName();
  }
}
