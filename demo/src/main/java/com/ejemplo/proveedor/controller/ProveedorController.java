// src/main/java/com/ejemplo/proveedor/controller/ProveedorController.java
package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.model.Proveedor;
import com.ejemplo.proveedor.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "http://localhost:4200")
public class ProveedorController {

  private final ProveedorService service;

  public ProveedorController(ProveedorService service) {
    this.service = service;
  }

  @GetMapping
  public List<Proveedor> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Proveedor> obtener(@PathVariable String id) {
    return service.obtener(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Proveedor> crear(@Valid @RequestBody Proveedor proveedor) {
    Proveedor guardado = service.guardar(proveedor);
    return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Proveedor> actualizar(@PathVariable String id,
      @Valid @RequestBody Proveedor proveedor) {
    return service.actualizar(id, proveedor)
        ? ResponseEntity.ok(service.obtener(id).orElseThrow())
        : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    return service.eliminar(id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}