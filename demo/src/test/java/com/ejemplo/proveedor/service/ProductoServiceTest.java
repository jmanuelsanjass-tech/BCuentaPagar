package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ejemplo.proveedor.controller.ProductoController;
import com.ejemplo.proveedor.dto.ProductoBusquedaResponse;
import com.ejemplo.proveedor.dto.ProductoRequest;
import com.ejemplo.proveedor.model.Producto;
import com.ejemplo.proveedor.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import com.ejemplo.proveedor.repository.ProveedorRepository;
import java.util.Optional;
import com.ejemplo.proveedor.model.Proveedor;

class ProductoServiceTest {

  @Test
  void crearAplicaValoresPorDefectoDeInventario() {

    ProductoRepository repository = mock(ProductoRepository.class);

    ProveedorRepository proveedorRepository = mock(ProveedorRepository.class);

    when(repository.saveAndFlush(any(Producto.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Proveedor proveedor = new Proveedor();
    proveedor.setIdProveedor(1L);

    when(proveedorRepository.findById(1L))
        .thenReturn(Optional.of(proveedor));

    ProductoService service = new ProductoService(
        repository,
        proveedorRepository);

    ProductoRequest datos = new ProductoRequest(
        "7501234567890",
        "Arroz",
        "",
        "Abarrotes secos y enlatados",
        new BigDecimal("25.50"),
        0,
        "activo",
        1L);

    Producto creado = service.crear(datos);

    assertEquals(
        "Abarrotes secos y enlatados",
        creado.getCategoria());

    assertEquals(0, creado.getStock());

    assertEquals("activo",
        creado.getEstado());
  }

  @Test
  void buscarPredictivoRecortaElNombreYLimitaResultados() {
    ProductoRepository repository = mock(ProductoRepository.class);
    ProveedorRepository proveedorRepository = mock(ProveedorRepository.class);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

    ProductoBusquedaResponse producto = new ProductoBusquedaResponse(
        1L,
        "7501234567890",
        "Café",
        "Café molido",
        "Abarrotes",
        new BigDecimal("89.50"),
        7);

    when(repository.buscarPredictivo(
        eq("cafe"),
        any(Pageable.class)))
        .thenReturn(List.of(producto));

    ProductoService service = new ProductoService(repository, proveedorRepository);

    List<ProductoBusquedaResponse> resultados = service.buscarPredictivo("  cafe  ");

    assertEquals(List.of(producto), resultados);

    verify(repository)
        .buscarPredictivo(eq("cafe"), pageableCaptor.capture());

    assertEquals(10, pageableCaptor.getValue().getPageSize());
  }

  @Test
  void buscarPredictivoAceptaElParametroLegadoTermino() {
    ProductoService service = mock(ProductoService.class);
    ProductoController controller = new ProductoController(service);
    when(service.buscarPredictivo("AR")).thenReturn(List.of());

    var response = controller.buscarPredictivo(null, "AR");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(service).buscarPredictivo("AR");
  }
}
