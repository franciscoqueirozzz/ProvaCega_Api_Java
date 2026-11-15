package br.com.fiap.controller;

import br.com.fiap.exception.RegraNegocioException;
import br.com.fiap.model.dto.AvaliacaoDTO;
import br.com.fiap.model.dto.NotaDTO;
import br.com.fiap.model.entity.Avaliacao;
import br.com.fiap.model.entity.Pessoa;
import br.com.fiap.model.entity.Resposta;
import br.com.fiap.model.repository.AvaliacaoRepository;
import br.com.fiap.model.repository.PessoaRepository;
import br.com.fiap.model.repository.RespostaRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    @Autowired
    private RespostaRepository respostaRepository;
    @Autowired
    private PessoaRepository pessoaRepository;

    /** Mnda um avaliador para uma resposta. */
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid AvaliacaoDTO dto) {
        Optional<Resposta> buscaResposta = respostaRepository.findById(dto.idResposta());
        if (buscaResposta.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resposta nao encontrada.");
        }
        Optional<Pessoa> buscaAvaliador = pessoaRepository.findById(dto.idAvaliador());
        if (buscaAvaliador.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Avaliador nao encontrado.");
        }
        try {
            buscaAvaliador.get().exigirPapel("AVALIADOR");
            if (avaliacaoRepository.existsByIdRespostaAndIdAvaliador(dto.idResposta(), dto.idAvaliador())) {
                throw new RegraNegocioException("Este avaliador ja foi designado para esta resposta.");
            }
            Resposta resposta = buscaResposta.get();
            resposta.iniciarAvaliacao();
            Avaliacao avaliacao = avaliacaoRepository.save(new Avaliacao(dto));
            respostaRepository.save(resposta);
            return ResponseEntity.status(HttpStatus.CREATED).body(avaliacao);
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao designar o avaliador.");
        }
    }

    @GetMapping
    public List<Avaliacao> consultar() {
        return avaliacaoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> consultarPorId(@PathVariable Long id) {
        Optional<Avaliacao> busca = avaliacaoRepository.findById(id);
        if (busca.isPresent()) {
            return ResponseEntity.ok(busca.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Avaliacao nao encontrada.");
    }

    @GetMapping("/resposta/{idResposta}")
    public ResponseEntity<?> consultarPorResposta(@PathVariable Long idResposta) {
        if (respostaRepository.existsById(idResposta)) {
            return ResponseEntity.ok(avaliacaoRepository.findByIdResposta(idResposta));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resposta nao encontrada.");
    }

    /** Registra a nota (0 a 10) e o parecer do avaliador. */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody @Valid NotaDTO dto) {
        Optional<Avaliacao> busca = avaliacaoRepository.findById(id);
        if (busca.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Avaliacao nao encontrada.");
        }
        try {
            Avaliacao avaliacao = busca.get();
            avaliacao.concluir(dto);
            avaliacaoRepository.save(avaliacao);
            return ResponseEntity.ok("Nota registrada com sucesso.");
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao registrar a nota.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        Optional<Avaliacao> busca = avaliacaoRepository.findById(id);
        if (busca.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Avaliacao nao encontrada.");
        }
        try {
            busca.get().exigirPendente();
            avaliacaoRepository.deleteById(id);
            return ResponseEntity.ok("Avaliacao excluida com sucesso.");
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nao foi possivel excluir a avaliacao.");
        }
    }
}