package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.model.Producto;
import com.ejemplo.proveedor.model.Proveedor;
import com.ejemplo.proveedor.dto.ProductoBusquedaResponse;
import com.ejemplo.proveedor.dto.ProductoRequest;
import com.ejemplo.proveedor.repository.ProductoRepository;
import com.ejemplo.proveedor.repository.ProveedorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

  private final ProductoRepository repository;
  private final ProveedorRepository proveedorRepository;

  public ProductoService(ProductoRepository repository, ProveedorRepository proveedorRepository) {
    this.repository = repository;
    this.proveedorRepository = proveedorRepository;
  }

  @Transactional(readOnly = true)
  public List<Producto> listar() {
    return repository.findAll();
  }

  // RESTAURADO: Método para buscar por nombre
  @Transactional(readOnly = true)
  public List<Producto> buscarPorNombre(String nombre) {
    return repository.findByNombreContainingIgnoreCase(nombre.trim());
  }

  // RESTAURADO: Método para la búsqueda predictiva
  @Transactional(readOnly = true)
  public List<ProductoBusquedaResponse> buscarPredictivo(String nombre) {
    return repository.buscarPredictivo(nombre.trim(), PageRequest.of(0, 10));
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
public Producto crear(
        ProductoRequest request) {

    Producto producto = new Producto();

    producto.setCodigoBarras(
        request.codigoBarras());

    producto.setNombre(
        request.nombre());

    producto.setDescripcion(
        request.descripcion());

    producto.setCategoria(
        request.categoria());

    producto.setPrecio(
        request.precio());

    producto.setStock(
        request.stock());

    producto.setEstado(
        request.estado());

    if (request.idProveedor() != null) {

        Proveedor proveedor =
            proveedorRepository.findById(
                request.idProveedor())
            .orElseThrow(() ->
                new RuntimeException(
                    "Proveedor no encontrado"));

        producto.setProveedor(
            proveedor);
    }

    return repository.saveAndFlush(
        producto);
}

  @Transactional
  public Optional<Producto> actualizar(
        Long idProducto,
        ProductoRequest request) {
return repository.findById(idProducto)
    .map(producto -> {

        producto.setCodigoBarras(
            request.codigoBarras());

        producto.setNombre(
            request.nombre());

        producto.setDescripcion(
            request.descripcion());

        producto.setCategoria(
            request.categoria());

        producto.setPrecio(
            request.precio());

        producto.setStock(
            request.stock());

        producto.setEstado(
            request.estado());

        if (request.idProveedor() != null) {

            Proveedor proveedor =
                proveedorRepository.findById(
                    request.idProveedor())
                .orElseThrow(() ->
                    new RuntimeException(
                        "Proveedor no encontrado"));

            producto.setProveedor(
                proveedor);
        }

        return repository.saveAndFlush(
            producto);
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
    producto.setCategoria(datos.getCategoria() == null ? "Abarrotes secos y enlatados" : datos.getCategoria());
    producto.setPrecio(datos.getPrecio());
    producto.setStock(datos.getStock() == null ? 0 : datos.getStock());
    producto.setEstado(datos.getEstado() == null ? "activo" : datos.getEstado());
    producto.setCodigoBarras(datos.getCodigoBarras());
    producto.setUsuarioAuditoria(usuarioAutenticado());
    producto.setQrCode(datos.getQrCode());
    producto.setProveedor(datos.getProveedor());

    // LÓGICA PARA ASIGNAR EL PROVEEDOR
    if (datos.getProveedor() != null && datos.getProveedor().getIdProveedor() != null) {
      Proveedor proveedor = proveedorRepository.findById(datos.getProveedor().getIdProveedor())
          .orElseThrow(
              () -> new RuntimeException("Proveedor no encontrado con ID: " + datos.getProveedor().getIdProveedor()));
      producto.setProveedor(proveedor);
    } else {
      producto.setProveedor(null);
    }
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