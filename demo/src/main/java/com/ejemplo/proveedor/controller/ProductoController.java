package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.model.Producto;
import com.ejemplo.proveedor.dto.ProductoBusquedaResponse;
import com.ejemplo.proveedor.dto.ProductoRequest;
import com.ejemplo.proveedor.service.ProductoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

  private final ProductoService service;

  public ProductoController(ProductoService service) {
    this.service = service;
  }

  @GetMapping
  public List<Producto> listar() {
    return service.listar();
  }

  @GetMapping("/buscar")
  public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
    if (nombre.isBlank() || nombre.length() > 100) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(service.buscarPorNombre(nombre));
  }

  @GetMapping("/buscar/predictivo")
  public ResponseEntity<List<ProductoBusquedaResponse>> buscarPredictivo(
      @RequestParam(name = "nombre", required = false) String nombre,
      @RequestParam(name = "termino", required = false) String termino) {
    if ((nombre == null) == (termino == null)) {
      return ResponseEntity.badRequest().build();
    }

    String criterio = nombre != null ? nombre : termino;
    if (criterio.isBlank() || criterio.trim().length() < 2 || criterio.length() > 100) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(service.buscarPredictivo(criterio));
  }

  @GetMapping("/categoria")
  public ResponseEntity<List<Producto>> buscarPorCategoria(@RequestParam String categoria) {
    // CORREGIDO: Lista actualizada con las 5 categorías reales
    List<String> categoriasValidas = List.of(
        "Abarrotes secos y enlatados",
        "Refrigerados y lácteos",
        "Frutas, verduras y panadería",
        "Bebidas y botanas",
        "Limpieza e higiene personal"
    );
    
    if (categoria.isBlank() || !categoriasValidas.contains(categoria.trim())) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(service.buscarPorCategoria(categoria));
  }

  @GetMapping("/id/{idProducto}")
  public ResponseEntity<Producto> obtenerPorId(@PathVariable Long idProducto) {
    return service.obtener(idProducto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/{codigoBarras}")
  public ResponseEntity<Producto> obtenerPorCodigoBarras(@PathVariable String codigoBarras) {
    return service.obtenerPorCodigoBarras(codigoBarras)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

@PostMapping
public ResponseEntity<Producto> crear(
        @Valid @RequestBody ProductoRequest request) {

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.crear(request));
}

@PutMapping("/id/{idProducto}")
public ResponseEntity<Producto> actualizarPorId(
        @PathVariable Long idProducto,
        @Valid @RequestBody ProductoRequest request) {

    return service.actualizar(idProducto, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

  @PutMapping("/{codigoBarras}")
  public ResponseEntity<Producto> actualizarPorCodigoBarras(
      @PathVariable String codigoBarras, @Valid @RequestBody Producto producto) {
    return service.actualizarPorCodigoBarras(codigoBarras, producto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/id/{idProducto}")
  public ResponseEntity<Void> eliminarPorId(@PathVariable Long idProducto) {
    return service.eliminar(idProducto)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{codigoBarras}")
  public ResponseEntity<Void> eliminarPorCodigoBarras(@PathVariable String codigoBarras) {
    return service.eliminarPorCodigoBarras(codigoBarras)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}