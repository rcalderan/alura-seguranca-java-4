

CREATE TYPE status_enum AS ENUM (
    'NAO_RESPONDIDO',
    'RESPONDIDO',
    'RESOLVIDO'
);

-- Criação da tabela topicos
CREATE TABLE topicos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    mensagem TEXT NOT NULL,
    autor VARCHAR(255) NOT NULL,
    categoria categoria_enum NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    status status_enum NOT NULL,
    aberto BOOLEAN NOT NULL,
    quantidade_respostas INTEGER NOT NULL,
    curso_id BIGINT,
    CONSTRAINT fk_curso FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE SET NULL
);
