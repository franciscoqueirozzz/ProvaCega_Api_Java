package br.com.fiap.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record NotaDTO(
        @NotNull @DecimalMin("0.00") @DecimalMax("10.00") BigDecimal nota,
        @NotBlank @Size(max = 500) String parecer) {
}
