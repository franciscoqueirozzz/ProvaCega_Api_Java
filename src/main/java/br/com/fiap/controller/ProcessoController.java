package br.com.fiap.controller;

import br.com.fiap.exception.RegraNegocioException;
import br.com.fiap.model.dto.ProcessoDTO;
import br.com.fiap.model.entity.CodigoAnonimo;
import br.com.fiap.model.entity.Pessoa;
import br.com.fiap.model.entity.Processo;
import br.com.fiap.model.repository.CodigoAnonimoRepository;
import br.com.fiap.model.repository.PessoaRepository;
import br.com.fiap.model.repository.ProcessoRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/processos")
public class ProcessoController {

    @Autowired
    private ProcessoRepository processoRepository;
    @Autowired
    private PessoaRepository pessoaRepository;
    @Autowired
    private CodigoAnonimoRepository codigoRepository;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid ProcessoDTO dto) {
        Optional<Pessoa> buscaOrganizador = pessoaRepository.findById(dto.idOrganizador());
        if (buscaOrganizador.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Organizador nao encontrado.");
        }
        try {
            buscaOrganizador.get().exigirPapel("ORGANIZADOR");
            Processo processo = processoRepository.save(new Processo(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(processo);
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao cadastrar o processo.");
        }
    }

    @GetMapping
    public List<Processo> consultar() {
        return processoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> consultarPorId(@PathVariable Long id) {
        Optional<Processo> busca = processoRepository.findById(id);
        if (busca.isPresent()) {
            return ResponseEntity.ok(busca.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo nao encontrado.");
    }

    /** Atualiza os dados. Se a situacao mudar, o processo avanca uma fase; ao ENCERRAR, os codigos expiram. */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody @Valid ProcessoDTO dto) {
        Optional<Processo> busca = processoRepository.findById(id);
        if (busca.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo nao encontrado.");
        }
        try {
            Processo processo = busca.get();
            processo.atualizar(dto);
            if (dto.situacao() != null && !dto.situacao().equals(processo.getSituacao())) {
                processo.avancarPara(dto.situacao());
                if ("ENCERRADO".equals(processo.getSituacao())) {
                    for (CodigoAnonimo codigo : codigoRepository.findByIdProcesso(id)) {
                        codigo.expirar();
                        codigoRepository.save(codigo);
                    }
                }
            }
            processoRepository.save(processo);
            return ResponseEntity.ok("Processo atualizado com sucesso.");
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar o processo.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!processoRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo nao encontrado.");
        }
        try {
            processoRepository.deleteById(id);
            return ResponseEntity.ok("Processo excluido com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Nao foi possivel excluir: o processo esta em uso em outros registros.");
        }
    }
}
