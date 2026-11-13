package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.OrdenCompraRequest;
import com.ejemplo.proveedor.dto.OrdenCompraResponse;
import com.ejemplo.proveedor.service.OrdenCompraService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
@RequestMapping("/api/ordenes-compra")
public class OrdenCompraController {

  private final OrdenCompraService service;

  public OrdenCompraController(OrdenCompraService service) {
    this.service = service;
  }

  @GetMapping
  public List<OrdenCompraResponse> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrdenCompraResponse> obtener(@PathVariable UUID id) {
    return service.obtener(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<OrdenCompraResponse> crear(
      @Valid @RequestBody OrdenCompraRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<OrdenCompraResponse> actualizar(
      @PathVariable UUID id, @Valid @RequestBody OrdenCompraRequest request) {
    return service.actualizar(id, request)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
    return service.eliminar(id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
