package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.CuentaPorCobrarRequest;
import com.ejemplo.proveedor.dto.CuentaPorCobrarResponse;
import com.ejemplo.proveedor.model.Cliente;
import com.ejemplo.proveedor.model.CuentaPorCobrar;
import com.ejemplo.proveedor.model.Venta;
import com.ejemplo.proveedor.repository.ClienteRepository;
import com.ejemplo.proveedor.repository.CuentaPorCobrarRepository;
import com.ejemplo.proveedor.repository.VentaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CuentaPorCobrarService {

  private final CuentaPorCobrarRepository repository;
  private final ClienteRepository clienteRepository;
  private final VentaRepository ventaRepository;

  public CuentaPorCobrarService(
      CuentaPorCobrarRepository repository,
      ClienteRepository clienteRepository,
      VentaRepository ventaRepository) {
    this.repository = repository;
    this.clienteRepository = clienteRepository;
    this.ventaRepository = ventaRepository;
  }

  @Transactional(readOnly = true)
  public List<CuentaPorCobrarResponse> listar() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<CuentaPorCobrarResponse> obtener(Long idCxc) {
    return repository.findById(idCxc).map(this::toResponse);
  }

  @Transactional
  public CuentaPorCobrarResponse crear(CuentaPorCobrarRequest request) {
    CuentaPorCobrar cuenta = new CuentaPorCobrar();
    aplicarRequest(cuenta, request);
    return toResponse(repository.saveAndFlush(cuenta));
  }

  @Transactional
  public Optional<CuentaPorCobrarResponse> actualizar(
      Long idCxc, CuentaPorCobrarRequest request) {
    return repository.findById(idCxc).map(cuenta -> {
      aplicarRequest(cuenta, request);
      return toResponse(repository.saveAndFlush(cuenta));
    });
  }

  @Transactional
  public boolean eliminar(Long idCxc) {
    return repository.findById(idCxc).map(cuenta -> {
      repository.delete(cuenta);
      return true;
    }).orElse(false);
  }

  private void aplicarRequest(CuentaPorCobrar cuenta, CuentaPorCobrarRequest request) {
    Cliente cliente = clienteRepository.findById(request.clienteId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No existe el cliente indicado"));
    Venta venta = ventaRepository.findById(request.ventaId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No existe la venta indicada"));

    if (!venta.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "La venta no pertenece al cliente indicado");
    }

    cuenta.setCliente(cliente);
    cuenta.setVenta(venta);
    cuenta.setFechaVenta(request.fechaVenta());
    cuenta.setFechaVencimiento(request.fechaVencimiento());
    cuenta.setImporteOriginal(defaultAmount(request.importeOriginal()));
    cuenta.setSaldoPendiente(defaultAmount(request.saldoPendiente()));
    cuenta.setDiasVencidos(request.diasVencidos() == null ? 0 : request.diasVencidos());
    cuenta.setLimiteCredito(defaultAmount(request.limiteCredito()));
    cuenta.setEstatus(request.estatus() == null ? "Pendiente" : request.estatus());
    cuenta.setFechaUltimoPago(request.fechaUltimoPago());
  }

  private BigDecimal defaultAmount(BigDecimal amount) {
    return amount == null ? BigDecimal.ZERO.setScale(2) : amount;
  }

  private CuentaPorCobrarResponse toResponse(CuentaPorCobrar cuenta) {
    return new CuentaPorCobrarResponse(
        cuenta.getIdCxc(),
        cuenta.getCliente().getIdCliente(),
        cuenta.getVenta().getIdVenta(),
        cuenta.getFechaVenta(),
        cuenta.getFechaVencimiento(),
        cuenta.getImporteOriginal(),
        cuenta.getSaldoPendiente(),
        cuenta.getDiasVencidos(),
        cuenta.getLimiteCredito(),
        cuenta.getEstatus(),
        cuenta.getFechaUltimoPago());
  }
}
