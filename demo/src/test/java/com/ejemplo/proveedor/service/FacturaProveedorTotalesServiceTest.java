package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ejemplo.proveedor.model.FacturaProveedor;
import com.ejemplo.proveedor.model.FacturaProveedorLinea;
import com.ejemplo.proveedor.repository.FacturaProveedorLineaRepository;
import com.ejemplo.proveedor.repository.FacturaProveedorRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FacturaProveedorTotalesServiceTest {

  @Test
  void recalcularDesdeLineasActualizaNetoImpuestosYTotal() {
    FacturaProveedorRepository facturaRepository = mock(FacturaProveedorRepository.class);
    FacturaProveedorLineaRepository lineaRepository =
        mock(FacturaProveedorLineaRepository.class);
    UUID facturaId = UUID.randomUUID();
    FacturaProveedor factura = new FacturaProveedor();
    factura.setId(facturaId);
    factura.setMontoPagado(BigDecimal.ZERO);

    FacturaProveedorLinea lineaUno = new FacturaProveedorLinea();
    lineaUno.setMontoNeto(new BigDecimal("10.0000"));
    lineaUno.setMontoImpuestos(new BigDecimal("1.6000"));
    FacturaProveedorLinea lineaDos = new FacturaProveedorLinea();
    lineaDos.setMontoNeto(new BigDecimal("5.0000"));
    lineaDos.setMontoImpuestos(new BigDecimal("0.8000"));

    when(facturaRepository.findById(facturaId)).thenReturn(Optional.of(factura));
    when(lineaRepository.findAllByFactura_IdOrderByNumeroLineaAsc(facturaId))
        .thenReturn(List.of(lineaUno, lineaDos));
    when(facturaRepository.saveAndFlush(any(FacturaProveedor.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    FacturaProveedorTotalesService service =
        new FacturaProveedorTotalesService(facturaRepository, lineaRepository);

    service.recalcularDesdeLineas(facturaId);

    assertEquals(new BigDecimal("15.0000"), factura.getMontoNeto());
    assertEquals(new BigDecimal("2.4000"), factura.getMontoImpuestos());
    assertEquals(new BigDecimal("17.4000"), factura.getMontoTotal());
  }
}
