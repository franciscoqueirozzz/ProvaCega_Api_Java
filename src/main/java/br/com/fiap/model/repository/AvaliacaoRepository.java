package br.com.fiap.model.repository;

import br.com.fiap.model.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByIdResposta(Long idResposta);
    boolean existsByIdRespostaAndIdAvaliador(Long idResposta, Long idAvaliador);
}
