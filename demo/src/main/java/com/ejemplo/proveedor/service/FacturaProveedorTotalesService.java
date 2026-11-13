package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.model.FacturaProveedor;
import com.ejemplo.proveedor.repository.FacturaProveedorLineaRepository;
import com.ejemplo.proveedor.repository.FacturaProveedorRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FacturaProveedorTotalesService {

  private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999999999.9999");

  private final FacturaProveedorRepository facturaRepository;
  private final FacturaProveedorLineaRepository lineaRepository;

  public FacturaProveedorTotalesService(
      FacturaProveedorRepository facturaRepository,
      FacturaProveedorLineaRepository lineaRepository) {
    this.facturaRepository = facturaRepository;
    this.lineaRepository = lineaRepository;
  }

  @Transactional
  public void recalcularDesdeLineas(UUID facturaId) {
    FacturaProveedor factura = obtenerFactura(facturaId);
    BigDecimal neto = BigDecimal.ZERO;
    BigDecimal impuestos = BigDecimal.ZERO;
    for (var linea : lineaRepository.findAllByFactura_IdOrderByNumeroLineaAsc(facturaId)) {
      neto = neto.add(linea.getMontoNeto());
      impuestos = impuestos.add(linea.getMontoImpuestos());
    }
    BigDecimal total = neto.add(impuestos);
    if (total.compareTo(MAX_AMOUNT) > 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Los importes de las líneas exceden el máximo permitido");
    }
    if (total.compareTo(factura.getMontoPagado()) < 0) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "El total de las líneas no puede ser menor que el monto pagado");
    }
    factura.setMontoNeto(neto);
    factura.setMontoImpuestos(impuestos);
    factura.setMontoTotal(total);
    if (factura.getMontoPagado().signum() > 0
        && factura.getMontoPagado().compareTo(total) == 0) {
      factura.setEstado("PAGADA");
    } else if ("PAGADA".equals(factura.getEstado())) {
      factura.setEstado("APROBADA_PAGO");
    }
    facturaRepository.saveAndFlush(factura);
  }

  @Transactional
  public void recalcularPagos(UUID facturaId) {
    FacturaProveedor factura = obtenerFactura(facturaId);
    BigDecimal montoPagado = facturaRepository.sumarPagosConfirmados(facturaId);
    if (montoPagado.compareTo(factura.getMontoTotal()) > 0) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Los pagos confirmados exceden el total de la factura");
    }
    factura.setMontoPagado(montoPagado);
    if (montoPagado.signum() > 0 && montoPagado.compareTo(factura.getMontoTotal()) == 0) {
      factura.setEstado("PAGADA");
    } else if ("PAGADA".equals(factura.getEstado())) {
      factura.setEstado("APROBADA_PAGO");
    }
    facturaRepository.saveAndFlush(factura);
  }

  private FacturaProveedor obtenerFactura(UUID facturaId) {
    return facturaRepository.findById(facturaId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No existe la factura indicada"));
  }
}
