package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.RolRequest;
import com.ejemplo.proveedor.dto.RolResponse;
import com.ejemplo.proveedor.service.RolService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RolController {

  private final RolService service;

  public RolController(RolService service) {
    this.service = service;
  }

  @GetMapping
  public List<RolResponse> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<RolResponse> obtener(@PathVariable Long id) {
    return service.obtener(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<RolResponse> crear(@Valid @RequestBody RolRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<RolResponse> actualizar(
      @PathVariable Long id, @Valid @RequestBody RolRequest request) {
    return service.actualizar(id, request)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    return service.eliminar(id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
