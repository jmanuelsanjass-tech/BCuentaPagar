BEGIN;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'usuarios' AND column_name = 'id'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'public' AND table_name = 'usuarios' AND column_name = 'id_usuario'
        ) THEN
            ALTER TABLE public.usuarios DROP COLUMN id_usuario;
        END IF;
        ALTER TABLE public.usuarios RENAME COLUMN id TO id_usuario;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'usuarios' AND column_name = 'enabled'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'public' AND table_name = 'usuarios' AND column_name = 'activo'
        ) THEN
            UPDATE public.usuarios SET activo = enabled;
            ALTER TABLE public.usuarios DROP COLUMN enabled;
        ELSE
            ALTER TABLE public.usuarios RENAME COLUMN enabled TO activo;
        END IF;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'usuarios' AND column_name = 'fecha_alta'
    ) THEN
        ALTER TABLE public.usuarios ADD COLUMN fecha_alta TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'roles' AND column_name = 'id'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'public' AND table_name = 'roles' AND column_name = 'id_rol'
        ) THEN
            ALTER TABLE public.roles DROP COLUMN id_rol;
        END IF;
        ALTER TABLE public.roles RENAME COLUMN id TO id_rol;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'roles' AND column_name = 'descripcion'
    ) THEN
        ALTER TABLE public.roles ADD COLUMN descripcion VARCHAR(200);
    END IF;
END
$$;

COMMIT;
