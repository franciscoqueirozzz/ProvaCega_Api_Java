package br.com.fiap.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PessoaDTO(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "deve ter 11 digitos (somente numeros)") String cpf,
        @NotBlank @Pattern(regexp = "PARTICIPANTE|ORGANIZADOR|AVALIADOR|REVISOR|AUDITOR|ADMINISTRADOR",
                message = "papel invalido") String papel) {
}
