package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.PoliticaAprobacionRequest;
import com.ejemplo.proveedor.dto.PoliticaAprobacionResponse;
import com.ejemplo.proveedor.model.PoliticaAprobacion;
import com.ejemplo.proveedor.repository.PoliticaAprobacionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PoliticaAprobacionService {

  private final PoliticaAprobacionRepository repository;
  private final UuidV7Generator uuidV7Generator;

  public PoliticaAprobacionService(
      PoliticaAprobacionRepository repository, UuidV7Generator uuidV7Generator) {
    this.repository = repository;
    this.uuidV7Generator = uuidV7Generator;
  }

  @Transactional(readOnly = true)
  public List<PoliticaAprobacionResponse> listar() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<PoliticaAprobacionResponse> obtener(UUID id) {
    return repository.findById(id).map(this::toResponse);
  }

  @Transactional
  public PoliticaAprobacionResponse crear(PoliticaAprobacionRequest request) {
    validar(request);
    PoliticaAprobacion politica = new PoliticaAprobacion();
    politica.setId(uuidV7Generator.generate());
    aplicarRequest(politica, request);
    return toResponse(repository.saveAndFlush(politica));
  }

  @Transactional
  public Optional<PoliticaAprobacionResponse> actualizar(
      UUID id, PoliticaAprobacionRequest request) {
    return repository.findById(id).map(politica -> {
      validar(request);
      aplicarRequest(politica, request);
      return toResponse(repository.saveAndFlush(politica));
    });
  }

  @Transactional
  public boolean eliminar(UUID id) {
    return repository.findById(id).map(politica -> {
      repository.delete(politica);
      return true;
    }).orElse(false);
  }

  private void validar(PoliticaAprobacionRequest request) {
    if (request.montoMaximo() != null
        && request.montoMinimo().compareTo(request.montoMaximo()) > 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "El monto máximo no puede ser menor que el mínimo");
    }
  }

  private void aplicarRequest(
      PoliticaAprobacion politica, PoliticaAprobacionRequest request) {
    politica.setEmpresaId(request.empresaId());
    politica.setTipoTransaccion(request.tipoTransaccion());
    politica.setMontoMinimo(request.montoMinimo());
    politica.setMontoMaximo(request.montoMaximo());
    politica.setRolRequerido(request.rolRequerido().trim());
    politica.setOrdenSecuencia(request.ordenSecuencia() == null ? (short) 1
        : request.ordenSecuencia());
    politica.setActivo(request.activo() == null || request.activo());
  }

  private PoliticaAprobacionResponse toResponse(PoliticaAprobacion politica) {
    return new PoliticaAprobacionResponse(
        politica.getId(),
        politica.getEmpresaId(),
        politica.getTipoTransaccion(),
        politica.getMontoMinimo(),
        politica.getMontoMaximo(),
        politica.getRolRequerido(),
        politica.getOrdenSecuencia(),
        politica.isActivo());
  }
}
