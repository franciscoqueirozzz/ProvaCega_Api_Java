package br.com.fiap.model.repository;

import br.com.fiap.model.entity.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RespostaRepository extends JpaRepository<Resposta, Long> {
    boolean existsByIdCodigo(Long idCodigo);
}
