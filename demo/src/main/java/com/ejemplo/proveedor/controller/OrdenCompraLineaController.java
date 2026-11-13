package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.OrdenCompraLineaRequest;
import com.ejemplo.proveedor.dto.OrdenCompraLineaResponse;
import com.ejemplo.proveedor.service.OrdenCompraLineaService;
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
@RequestMapping("/api/ordenes-compra/{ordenCompraId}/lineas")
public class OrdenCompraLineaController {

  private final OrdenCompraLineaService service;

  public OrdenCompraLineaController(OrdenCompraLineaService service) {
    this.service = service;
  }

  @GetMapping
  public List<OrdenCompraLineaResponse> listar(@PathVariable UUID ordenCompraId) {
    return service.listar(ordenCompraId);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrdenCompraLineaResponse> obtener(
      @PathVariable UUID ordenCompraId, @PathVariable UUID id) {
    return service.obtener(ordenCompraId, id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<OrdenCompraLineaResponse> crear(
      @PathVariable UUID ordenCompraId, @Valid @RequestBody OrdenCompraLineaRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(ordenCompraId, request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<OrdenCompraLineaResponse> actualizar(
      @PathVariable UUID ordenCompraId,
      @PathVariable UUID id,
      @Valid @RequestBody OrdenCompraLineaRequest request) {
    return service.actualizar(ordenCompraId, id, request)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(
      @PathVariable UUID ordenCompraId, @PathVariable UUID id) {
    return service.eliminar(ordenCompraId, id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
