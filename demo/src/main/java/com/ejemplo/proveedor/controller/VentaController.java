package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.VentaRequest;
import com.ejemplo.proveedor.dto.VentaResponse;
import com.ejemplo.proveedor.service.VentaService;
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
@RequestMapping("/api/ventas")
public class VentaController {

  private final VentaService service;

  public VentaController(VentaService service) {
    this.service = service;
  }

  @GetMapping
  public List<VentaResponse> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<VentaResponse> obtener(@PathVariable Long id) {
    return service.obtener(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<VentaResponse> crear(@Valid @RequestBody VentaRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<VentaResponse> actualizar(
      @PathVariable Long id, @Valid @RequestBody VentaRequest request) {
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
