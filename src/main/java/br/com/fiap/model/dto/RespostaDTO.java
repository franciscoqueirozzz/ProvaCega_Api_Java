package br.com.fiap.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Envio de resposta: quem envia se identifica so pelo codigo anonimo. */
public record RespostaDTO(
        @NotNull Long idProcesso,
        @NotBlank @Size(max = 12) String codigo,
        @NotBlank String conteudo) {
}
