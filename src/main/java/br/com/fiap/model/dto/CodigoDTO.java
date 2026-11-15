package br.com.fiap.model.dto;

import jakarta.validation.constraints.NotNull;

/** Pedido de emissao de codigo: qual processo e qual participante. */
public record CodigoDTO(@NotNull Long idProcesso, @NotNull Long idPessoa) {
}
