// src/main/java/com/ejemplo/proveedor/controller/ProveedorController.java
package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.model.Proveedor;
import com.ejemplo.proveedor.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "http://localhost:4200")
public class ProveedorController {

  @Autowired
  private ProveedorService service;

  @GetMapping
  public List<Proveedor> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Proveedor> obtener(@PathVariable Long id) {
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
  public ResponseEntity<Proveedor> actualizar(@PathVariable Long id,
      @Valid @RequestBody Proveedor proveedor) {
    proveedor.setId(id);
    Proveedor actualizado = service.guardar(proveedor);
    return ResponseEntity.ok(actualizado);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    service.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}