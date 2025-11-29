package br.com.forum_hub.domain.autenticacao.model;

import jakarta.validation.constraints.NotBlank;

public record DadosRefreshToken(@NotBlank String refreshToken) {
}
