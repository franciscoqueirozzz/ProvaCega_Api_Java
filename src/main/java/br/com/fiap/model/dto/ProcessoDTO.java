package br.com.fiap.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProcessoDTO(
        @NotNull Long idOrganizador,
        @NotBlank @Size(max = 150) String nome,
        @Size(max = 400) String descricao,
        @NotNull LocalDate dataInicio,
        @NotNull LocalDate dataFim,
        LocalDate prazoContestacao,
        @NotNull @DecimalMin("0.01") @DecimalMax("10.00") BigDecimal diferencaMaxima,
        @Pattern(regexp = "EM_CONFIGURACAO|ABERTO|EM_AVALIACAO|ENCERRADO", message = "situacao invalida") String situacao) {
}
