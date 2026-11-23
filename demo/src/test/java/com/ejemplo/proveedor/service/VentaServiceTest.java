package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class VentaServiceTest {

  @Test
  void calcularPagoEnEfectivoCalculaElCambio() {
    VentaService.PagoVenta pago = VentaService.calcularPago(
        new BigDecimal("84.50"), new BigDecimal("100.00"), "Pesos");

    assertEquals(new BigDecimal("100.00"), pago.montoRecibido());
    assertEquals(new BigDecimal("15.50"), pago.montoSobrante());
    assertEquals("Pesos", pago.tipoMoneda());
  }

  @Test
  void calcularPagoConTarjetaUsaElTotalSinCambio() {
    VentaService.PagoVenta pago = VentaService.calcularPago(
        new BigDecimal("84.50"), new BigDecimal("100.00"), "Tarjeta de Crédito");

    assertEquals(new BigDecimal("84.50"), pago.montoRecibido());
    assertEquals(new BigDecimal("0.00"), pago.montoSobrante());
  }

  @Test
  void calcularPagoEnEfectivoRechazaUnMontoMenorAlTotal() {
    assertThrows(ResponseStatusException.class, () -> VentaService.calcularPago(
        new BigDecimal("84.50"), new BigDecimal("84.49"), "Pesos"));
  }
}
