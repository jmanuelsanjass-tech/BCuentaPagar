# Punto de Venta ERP

## Nota técnica del proyecto

Este repositorio contiene la base de un backend para la gestión de cuentas por pagar, con foco en operaciones de negocio relacionadas con clientes, productos, proveedores y ventas. La aplicación está implementada en Java con Spring Boot y usa PostgreSQL como motor de persistencia.

La lógica principal vive en la carpeta `demo/`, donde se encuentra el proyecto ejecutable. La colección de Postman en `postman/collections/` sirve como referencia para probar los endpoints de la API.

## Objetivo del sistema

El backend permite:

- Registrar y consultar clientes.
- Gestionar productos y su inventario.
- Administrar proveedores.
- Registrar ventas con detalle de productos.
- Autenticar usuarios mediante sesión HTTP con Spring Security.
- Monitorear la salud del servicio a través de Spring Actuator.

## Stack tecnológico

- Java 21
- Spring Boot 4.2.0-SNAPSHOT
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL 15
- Maven
- Docker / Docker Compose
- Postman para pruebas manuales

## Estructura del repositorio

- `demo/`: proyecto principal de Spring Boot.
- `demo/src/main/java/com/ejemplo/proveedor`: paquetes de dominio, servicios, controladores y repositorios.
- `demo/src/main/resources`: configuración de entorno (`application-dev.properties`, `application-prod.properties`, `application-test.properties`).
- `demo/docker-compose.yaml`: infraestructura local con PostgreSQL y backend.
- `postman/collections/Cuentas-por-Pagar.postman_collection.json`: colección de pruebas de la API.

## Arquitectura

La solución sigue una arquitectura por capas:

- `controller`: expone endpoints REST.
- `service`: encapsula la lógica de negocio.
- `repository`: persistencia con Spring Data JPA.
- `model`: entidades del dominio.
- `dto`: objetos de entrada y salida.
- `config`: configuración de seguridad, CORS y autenticación.

### Entidades principales

- `Cliente`: información del cliente, dirección, estado y auditoría.
- `Producto`: código de barras, nombre, costo y relación con inventario.
- `Proveedor`: dato de proveedor para compras y relación comercial.
- `Venta`: cabecera de la venta con total, fecha y estado.
- `VentaDetalle`: líneas o conceptos incluidos en la venta.
- `Usuario` y `Rol`: modelo de seguridad y autorización.

## Seguridad

La API utiliza Spring Security con autenticación basada en sesión. Los usuarios se registran y validan mediante endpoints bajo `/api/auth`.

Configuración relevante:

- CSRF deshabilitado.
- CORS habilitado para `https://zapateria-erp-front.web.app` y `http://localhost:4200`,
  con credenciales para la autenticación basada en sesión.
- En producción, la cookie de sesión usa `SameSite=None; Secure` para que el navegador
  pueda enviarla entre el frontend y la API. El cliente debe enviar solicitudes con
  credenciales habilitadas.
- Endpoints públicos:
  - `/`
  - `/actuator/health`
  - `/api/auth/**`
- El resto de endpoints requiere autenticación.

El usuario inicial se crea con el rol `ADMIN` al arrancar la aplicación si existen las variables `APP_SECURITY_USERNAME` y `APP_SECURITY_PASSWORD` en el archivo `.env` de `demo/`. Si ese usuario ya existe, se conserva su contraseña cuando coincide con la variable y se le agrega el rol `ADMIN` si aún no lo tiene.

### CRUD de usuarios y roles

Las rutas `/api/usuarios/**` y `/api/roles/**` requieren el rol `ADMIN`.

Usuarios:

- `GET /api/usuarios` y `GET /api/usuarios/{id}`
- `POST /api/usuarios` para crear; acepta `username`, `password`, `activo` y `roles` (IDs de rol).
- `PUT /api/usuarios/{id}` para actualizar; acepta `username`, `password` opcional, `activo` y `roles` opcional.
- `DELETE /api/usuarios/{id}`

Roles:

- `GET /api/roles` y `GET /api/roles/{id}`
- `POST /api/roles` y `PUT /api/roles/{id}`; aceptan `nombre` y `descripcion`.
- `DELETE /api/roles/{id}`; devuelve conflicto si el rol está asignado a usuarios.

Las respuestas de usuario no incluyen la contraseña. Las contraseñas recibidas por el CRUD se almacenan cifradas con BCrypt.

Si la base de datos ya existía con las columnas `usuarios.enabled` y `roles.id`, ejecuta una vez `demo/src/main/resources/db/manual/migrate_usuarios_roles.sql` sobre la base `Tienda` antes de iniciar la nueva versión. Esta migración conserva los IDs y la relación `usuarios_roles`.

## Base de datos

El proyecto está configurado para usar PostgreSQL con la siguiente base por defecto:

- Base de datos: `Tienda`
- Usuario: `postgres`
- Puerto: `5432`

La configuración se define en `demo/src/main/resources/application-dev.properties` y se sobrescribe con variables de entorno en Docker.

## Ejecución con Docker

1. Desde la raíz del repositorio, crea el archivo de entorno:

   ```bash
   copy demo\.env.example demo\.env
   ```

2. Ajusta los valores de `demo/.env`:

   ```env
   DB_PASSWORD=tu_password_seguro_db
   APP_SECURITY_USERNAME=admin
   APP_SECURITY_PASSWORD=tu_password_seguro
   ```

3. Levanta los servicios:

   ```bash
   docker compose -f demo/docker-compose.yaml up --build
   ```

Esto iniciará la base de datos PostgreSQL y la API backend en el puerto `8080`.

## Ejecución local sin Docker

Desde la carpeta `demo/`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

En Windows PowerShell:

```powershell
./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

Asegúrate de definir las variables de entorno necesarias antes de iniciar la aplicación.

## Endpoints principales

### Autenticación

- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/logout`

### Clientes

- `GET /api/clientes`
- `GET /api/clientes/{id}`
- `POST /api/clientes`
- `PUT /api/clientes/{id}`
- `DELETE /api/clientes/{id}`

### Productos

- `GET /api/productos`
- `GET /api/productos/buscar?nombre={valor}`
- `GET /api/productos/categoria?categoria={Abarrotes|Lácteos|Frutería}`
- `GET /api/productos/id/{id}`
- `GET /api/productos/{codigoBarras}`
- `POST /api/productos`
- `PUT /api/productos/id/{id}`
- `PUT /api/productos/{codigoBarras}`
- `DELETE /api/productos/id/{id}`
- `DELETE /api/productos/{codigoBarras}`

Los productos se persisten en `inventario`. El código de barras es único; las categorías permitidas son `Abarrotes`, `Lácteos` y `Frutería`. Si se omiten, categoría, stock y estado toman por defecto `Abarrotes`, `0` y `activo`. El precio debe ser mayor que cero y el stock no puede ser negativo.

### Proveedores

- `GET /api/proveedores`
- `GET /api/proveedores/{id}`
- `POST /api/proveedores`
- `PUT /api/proveedores/{id}`
- `DELETE /api/proveedores/{id}`

### Ventas

- `GET /api/ventas`
- `GET /api/ventas/{id}`
- `POST /api/ventas`
- `PUT /api/ventas/{id}`
- `DELETE /api/ventas/{id}`

En las operaciones de creación y actualización, el subtotal se calcula a partir de los detalles; el descuento es opcional (por defecto `0`) y el IVA es obligatorio. El total se calcula como `subtotal - descuento + iva`. La venta requiere un cliente existente.

### Cuentas por cobrar

- `GET /api/cuentas-por-cobrar`
- `GET /api/cuentas-por-cobrar/{id}`
- `POST /api/cuentas-por-cobrar`
- `PUT /api/cuentas-por-cobrar/{id}`
- `DELETE /api/cuentas-por-cobrar/{id}`

Cada cuenta requiere un cliente y una venta existentes; la venta debe pertenecer al cliente indicado. Importe, saldo, días vencidos y límite de crédito toman `0` por defecto. Los estatus admitidos son `Pendiente`, `Pagado` y `Cancelado`.

### Historial de cuentas por cobrar

- `GET /api/cuentas-por-cobrar-historia`
- `GET /api/cuentas-por-cobrar-historia/{id}`
- `POST /api/cuentas-por-cobrar-historia`
- `PUT /api/cuentas-por-cobrar-historia/{id}`
- `DELETE /api/cuentas-por-cobrar-historia/{id}`

El alta requiere `idHistoria`, ya que la tabla no define una secuencia ni un valor predeterminado para esa clave. Cada movimiento debe referenciar una cuenta por cobrar existente. Los tipos admitidos son `ALTA`, `ABONO`, `Cancelado`, `PAGO_TOTAL`, `REESTRUCTURA`, `PRORROGA`, `CANCELACION`, `CASTIGO`, `BLOQUEO_CREDITO` y `DESBLOQUEO_CREDITO`. La eliminación de una cuenta con movimientos históricos está restringida por la llave foránea.

### Salud del sistema

- `GET /`
- `GET /actuator/health`

## Colección de Postman

La colección oficial de pruebas de la API se encuentra en:

- `postman/collections/Cuentas-por-Pagar.postman_collection.json`

Incluye ejemplos para autenticación, clientes, productos, proveedores, ventas y cuentas por cobrar con su historial.

## Observaciones técnicas relevantes

- El backend está preparado para trabajar con datos reales en PostgreSQL y con perfiles de entorno (`dev`, `prod`, `test`).
- El usuario base se inicializa automáticamente al arrancar la aplicación para facilitar la autenticación en entornos locales.
- El proyecto no incluye frontend ni UI; está pensado como API backend y expone servicios REST para consumo desde una aplicación cliente.
- La solución es extensible y modular, por lo que puede ampliarse con más módulos financieros, reportes, facturación y validaciones avanzadas.

## Conclusión

Este proyecto constituye una base sólida para un sistema de cuentas por pagar con gestión operativa de ventas y clientes, diseñado con buenas prácticas de Spring Boot, persistencia JPA y seguridad basada en sesión. Su estructura permite escalar a funcionalidades más complejas sin romper la organización del código.
