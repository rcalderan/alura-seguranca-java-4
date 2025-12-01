package br.com.forum_hub.domain.autenticacao.model;

import jakarta.validation.constraints.NotBlank;

public record Dados2Fa(@NotBlank String email,
                       @NotBlank String code) {
}
