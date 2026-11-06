package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.model.Producto;
import com.ejemplo.proveedor.service.ProductoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping("/categoria")
  public ResponseEntity<List<Producto>> buscarPorCategoria(@RequestParam String categoria) {
    if (categoria.isBlank()
        || !List.of("Abarrotes", "Lácteos", "Frutería").contains(categoria.trim())) {
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
  public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(producto));
  }

  @PutMapping("/id/{idProducto}")
  public ResponseEntity<Producto> actualizarPorId(
      @PathVariable Long idProducto, @Valid @RequestBody Producto producto) {
    return service.actualizar(idProducto, producto)
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
