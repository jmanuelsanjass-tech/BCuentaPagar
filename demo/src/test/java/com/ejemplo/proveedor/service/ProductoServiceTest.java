package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ejemplo.proveedor.model.Producto;
import com.ejemplo.proveedor.repository.ProductoRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductoServiceTest {

  @Test
  void crearAplicaValoresPorDefectoDeInventario() {
    ProductoRepository repository = mock(ProductoRepository.class);
    when(repository.saveAndFlush(any(Producto.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    ProductoService service = new ProductoService(repository);

    Producto datos = new Producto();
    datos.setNombre("Arroz");
    datos.setPrecio(new BigDecimal("25.50"));
    datos.setCodigoBarras("7501234567890");

    Producto creado = service.crear(datos);

    assertEquals("Abarrotes", creado.getCategoria());
    assertEquals(0, creado.getStock());
    assertEquals("activo", creado.getEstado());
  }
}
