CREATE TABLE public.bitacora_usuarios (
    id_evento BIGSERIAL PRIMARY KEY,
    administrador VARCHAR(100) NOT NULL,
    id_usuario_objetivo BIGINT,
    username_objetivo VARCHAR(100) NOT NULL,
    accion VARCHAR(50) NOT NULL,
    detalle VARCHAR(500),
    fecha_evento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bitacora_usuarios_fecha
    ON public.bitacora_usuarios (fecha_evento DESC);
