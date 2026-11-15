package br.com.fiap.model.repository;

import br.com.fiap.model.entity.CodigoAnonimo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CodigoAnonimoRepository extends JpaRepository<CodigoAnonimo, Long> {
    List<CodigoAnonimo> findByIdProcesso(Long idProcesso);
    CodigoAnonimo findByIdProcessoAndCodigo(Long idProcesso, String codigo);
    boolean existsByIdProcessoAndCodigo(Long idProcesso, String codigo);
}
