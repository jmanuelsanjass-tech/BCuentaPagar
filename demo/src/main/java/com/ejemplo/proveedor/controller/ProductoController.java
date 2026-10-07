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
    if (nombre.isBlank() || nombre.length() > 255) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(service.buscarPorNombre(nombre));
  }

  @GetMapping("/{codigoBarras}")
  public ResponseEntity<Producto> obtener(@PathVariable String codigoBarras) {
    return service.obtener(codigoBarras)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(producto));
  }

  @PutMapping("/{codigoBarras}")
  public ResponseEntity<Producto> actualizar(
      @PathVariable String codigoBarras, @Valid @RequestBody Producto producto) {
    return service.actualizar(codigoBarras, producto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{codigoBarras}")
  public ResponseEntity<Void> eliminar(@PathVariable String codigoBarras) {
    return service.eliminar(codigoBarras)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
