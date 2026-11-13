package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.CuentaContableResponse;
import com.ejemplo.proveedor.repository.CuentaContableRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuentaContableService {

  private final CuentaContableRepository repository;

  public CuentaContableService(CuentaContableRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<CuentaContableResponse> listar() {
    return repository.findAllByOrderByCodigoCuentaAsc().stream()
        .map(cuenta -> new CuentaContableResponse(
            cuenta.getId(),
            cuenta.getEmpresaId(),
            cuenta.getCodigoCuenta(),
            cuenta.getNombreCuenta(),
            cuenta.getTipoCuenta(),
            cuenta.isActivo(),
            cuenta.getCreadoEn()))
        .toList();
  }

  @Transactional(readOnly = true)
  public Optional<CuentaContableResponse> obtener(UUID id) {
    return repository.findById(id).map(cuenta -> new CuentaContableResponse(
        cuenta.getId(),
        cuenta.getEmpresaId(),
        cuenta.getCodigoCuenta(),
        cuenta.getNombreCuenta(),
        cuenta.getTipoCuenta(),
        cuenta.isActivo(),
        cuenta.getCreadoEn()));
  }
}
