package br.com.fiap.controller;

import br.com.fiap.exception.RegraNegocioException;
import br.com.fiap.model.dto.RespostaDTO;
import br.com.fiap.model.entity.Avaliacao;
import br.com.fiap.model.entity.CodigoAnonimo;
import br.com.fiap.model.entity.Processo;
import br.com.fiap.model.entity.Resposta;
import br.com.fiap.model.repository.AvaliacaoRepository;
import br.com.fiap.model.repository.CodigoAnonimoRepository;
import br.com.fiap.model.repository.ProcessoRepository;
import br.com.fiap.model.repository.RespostaRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Respostas: quem avalia enxerga apenas o codigo anonimo, nunca a pessoa. */
@RestController
@RequestMapping("/respostas")
public class RespostaController {

    @Autowired
    private RespostaRepository respostaRepository;
    @Autowired
    private CodigoAnonimoRepository codigoRepository;
    @Autowired
    private ProcessoRepository processoRepository;
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    /** O envio e feito somente com o codigo anonimo. */
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid RespostaDTO dto) {
        Optional<Processo> buscaProcesso = processoRepository.findById(dto.idProcesso());
        if (buscaProcesso.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo nao encontrado.");
        }
        CodigoAnonimo codigo = codigoRepository.findByIdProcessoAndCodigo(dto.idProcesso(), dto.codigo());
        if (codigo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Codigo nao encontrado neste processo.");
        }
        try {
            buscaProcesso.get().exigirAberto();
            codigo.exigirAtivo();
            if (respostaRepository.existsByIdCodigo(codigo.getIdCodigo())) {
                throw new RegraNegocioException("Este codigo ja enviou uma resposta.");
            }
            Resposta resposta = respostaRepository.save(new Resposta(dto, codigo.getIdCodigo()));
            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao enviar a resposta.");
        }
    }

    @GetMapping
    public List<Resposta> consultar() {
        return respostaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> consultarPorId(@PathVariable Long id) {
        Optional<Resposta> busca = respostaRepository.findById(id);
        if (busca.isPresent()) {
            return ResponseEntity.ok(busca.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resposta nao encontrada.");
    }

    /** Fecha a resposta: a nota final e a media das avaliacoes, e a aprovacao exige nota 7 ou mais. */
    @PutMapping("/{id}/fechar")
    public ResponseEntity<?> fechar(@PathVariable Long id) {
        Optional<Resposta> busca = respostaRepository.findById(id);
        if (busca.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resposta nao encontrada.");
        }
        try {
            Resposta resposta = busca.get();
            List<Avaliacao> avaliacoes = avaliacaoRepository.findByIdResposta(id);
            if (avaliacoes.isEmpty()) {
                throw new RegraNegocioException("A resposta ainda nao possui avaliacoes.");
            }
            BigDecimal soma = BigDecimal.ZERO;
            for (Avaliacao avaliacao : avaliacoes) {
                avaliacao.exigirConcluida();
                soma = soma.add(avaliacao.getNota());
            }
            resposta.fechar(soma.divide(BigDecimal.valueOf(avaliacoes.size()), 2, RoundingMode.HALF_UP));
            respostaRepository.save(resposta);
            return ResponseEntity.ok("Resposta fechada com sucesso.");
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao fechar a resposta.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        Optional<Resposta> busca = respostaRepository.findById(id);
        if (busca.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resposta nao encontrada.");
        }
        try {
            if (!"ENVIADA".equals(busca.get().getSituacao())) {
                throw new RegraNegocioException("So e possivel excluir respostas ENVIADA (ainda sem avaliador).");
            }
            respostaRepository.deleteById(id);
            return ResponseEntity.ok("Resposta excluida com sucesso.");
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nao foi possivel excluir a resposta.");
        }
    }
}
