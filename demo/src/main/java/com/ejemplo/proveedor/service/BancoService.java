package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.BancoRequest;
import com.ejemplo.proveedor.dto.BancoResponse;
import com.ejemplo.proveedor.model.Banco;
import com.ejemplo.proveedor.repository.BancoRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BancoService {

  private final BancoRepository repository;

  public BancoService(BancoRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<BancoResponse> listar() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<BancoResponse> obtener(UUID id) {
    return repository.findById(id).map(this::toResponse);
  }

  @Transactional
  public BancoResponse crear(BancoRequest request) {
    if (repository.existsByCodigoBanco(request.codigoBanco().trim())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "El código de banco ya existe");
    }

    Banco banco = new Banco();
    aplicarRequest(banco, request);
    banco.setActualizadoEn(OffsetDateTime.now());
    return toResponse(repository.saveAndFlush(banco));
  }

  @Transactional
  public Optional<BancoResponse> actualizar(UUID id, BancoRequest request) {
    return repository.findById(id).map(banco -> {
      if (repository.existsByCodigoBancoAndIdNot(request.codigoBanco().trim(), id)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "El código de banco ya existe");
      }
      aplicarRequest(banco, request);
      banco.setActualizadoEn(OffsetDateTime.now());
      return toResponse(repository.saveAndFlush(banco));
    });
  }

  @Transactional
  public boolean eliminar(UUID id) {
    return repository.findById(id).map(banco -> {
      repository.delete(banco);
      return true;
    }).orElse(false);
  }

  private void aplicarRequest(Banco banco, BancoRequest request) {
    banco.setCodigoBanco(request.codigoBanco().trim());
    banco.setNombre(request.nombre().trim());
    banco.setNombreCorto(limpiarOpcional(request.nombreCorto()));
    banco.setCodigoSwiftBic(limpiarOpcional(request.codigoSwiftBic(), true));
    banco.setPais(request.pais() == null || request.pais().isBlank()
        ? "MX"
        : request.pais().trim().toUpperCase());
    banco.setActivo(request.activo() == null || request.activo());
  }

  private String limpiarOpcional(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }

  private String limpiarOpcional(String value, boolean uppercase) {
    String limpio = limpiarOpcional(value);
    return limpio == null || !uppercase ? limpio : limpio.toUpperCase();
  }

  private BancoResponse toResponse(Banco banco) {
    return new BancoResponse(
        banco.getId(),
        banco.getCodigoBanco(),
        banco.getNombre(),
        banco.getNombreCorto(),
        banco.getCodigoSwiftBic(),
        banco.getPais(),
        banco.isActivo(),
        banco.getCreadoEn(),
        banco.getActualizadoEn());
  }
}
