-- Adiciona vínculo opcional de Relatorio com Especialista
ALTER TABLE Relatorio
    ADD COLUMN IF NOT EXISTS especialista_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_relatorio_especialista'
    ) THEN
        ALTER TABLE Relatorio
            ADD CONSTRAINT fk_relatorio_especialista
            FOREIGN KEY (especialista_id) REFERENCES Especialista(usuario_id);
    END IF;
END
$$;

CREATE INDEX IF NOT EXISTS idx_relatorio_especialista ON Relatorio (especialista_id);
