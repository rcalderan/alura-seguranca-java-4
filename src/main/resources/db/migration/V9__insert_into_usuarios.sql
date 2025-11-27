--initial user pass=12345678

INSERT INTO usuarios (email, senha, nome_completo, nome_usuario, verificado, ativo)
VALUES (
    '<seu email do google/github>',
    '$2y$10$/8EepTsy2reD5vKLPGn.gOXfFs5b6jjoaaWx/79kvdAfu0U0f4vmS',
    'Usuario',
    'usuario',
    TRUE,
    TRUE
);