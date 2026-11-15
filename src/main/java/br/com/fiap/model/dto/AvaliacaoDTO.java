package br.com.fiap.model.dto;

import jakarta.validation.constraints.NotNull;

/** Designacao de um avaliador para uma resposta. */
public record AvaliacaoDTO(@NotNull Long idResposta, @NotNull Long idAvaliador) {
}
