package br.com.fiap.model.repository;

import br.com.fiap.model.entity.Identidade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IdentidadeRepository extends JpaRepository<Identidade, Long> {
    boolean existsByIdProcessoAndIdPessoa(Long idProcesso, Long idPessoa);
    List<Identidade> findByIdPessoa(Long idPessoa);
}
