package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.ClienteRequest;
import com.ejemplo.proveedor.dto.ClienteResponse;
import com.ejemplo.proveedor.service.ClienteService;
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
@RequestMapping("/api/clientes")
public class ClienteController {

  private final ClienteService service;

  public ClienteController(ClienteService service) {
    this.service = service;
  }

  @GetMapping
  public List<ClienteResponse> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClienteResponse> obtener(@PathVariable Long id) {
    return service.obtener(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ClienteResponse> actualizar(
      @PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
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
