package br.com.fiap.model.dto;

import jakarta.validation.constraints.NotNull;

/** Pedido de emissao de codigo*/
public record CodigoDTO(@NotNull Long idProcesso, @NotNull Long idPessoa) {
}
