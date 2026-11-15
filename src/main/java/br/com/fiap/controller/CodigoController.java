package br.com.fiap.controller;

import br.com.fiap.exception.RegraNegocioException;
import br.com.fiap.model.dto.CodigoDTO;
import br.com.fiap.model.entity.CodigoAnonimo;
import br.com.fiap.model.entity.Identidade;
import br.com.fiap.model.entity.Pessoa;
import br.com.fiap.model.repository.CodigoAnonimoRepository;
import br.com.fiap.model.repository.IdentidadeRepository;
import br.com.fiap.model.repository.PessoaRepository;
import br.com.fiap.model.repository.ProcessoRepository;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Codigos anonimos, A ligacao entre pessoa e codigo fica guardada no cofre tb_identidade. */
@RestController
@RequestMapping("/codigos")
public class CodigoController {

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    @Autowired
    private CodigoAnonimoRepository codigoRepository;
    @Autowired
    private IdentidadeRepository identidadeRepository;
    @Autowired
    private PessoaRepository pessoaRepository;
    @Autowired
    private ProcessoRepository processoRepository;

    /** Gera um codigo aleatorio para o participante e grava o vinculo no cofre. */
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid CodigoDTO dto) {
        if (!processoRepository.existsById(dto.idProcesso())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo nao encontrado.");
        }
        Optional<Pessoa> buscaPessoa = pessoaRepository.findById(dto.idPessoa());
        if (buscaPessoa.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pessoa nao encontrada.");
        }
        try {
            buscaPessoa.get().exigirPapel("PARTICIPANTE");
            if (identidadeRepository.existsByIdProcessoAndIdPessoa(dto.idProcesso(), dto.idPessoa())) {
                throw new RegraNegocioException("A pessoa ja possui codigo neste processo.");
            }
            CodigoAnonimo codigo = codigoRepository.save(new CodigoAnonimo(dto, gerarCodigo(dto.idProcesso())));
            identidadeRepository.save(new Identidade(dto, codigo.getIdCodigo()));
            return ResponseEntity.status(HttpStatus.CREATED).body(codigo); // so o codigo, sem dados da pessoa
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao emitir o codigo.");
        }
    }

    @GetMapping
    public List<CodigoAnonimo> consultar() {
        return codigoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> consultarPorId(@PathVariable Long id) {
        Optional<CodigoAnonimo> busca = codigoRepository.findById(id);
        if (busca.isPresent()) {
            return ResponseEntity.ok(busca.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Codigo nao encontrado.");
    }

    @GetMapping("/processo/{idProcesso}")
    public ResponseEntity<?> consultarPorProcesso(@PathVariable Long idProcesso) {
        if (processoRepository.existsById(idProcesso)) {
            return ResponseEntity.ok(codigoRepository.findByIdProcesso(idProcesso));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo nao encontrado.");
    }

    /** O participante consulta os proprios codigos. */
    @GetMapping("/pessoa/{idPessoa}")
    public ResponseEntity<?> consultarPorPessoa(@PathVariable Long idPessoa) {
        if (!pessoaRepository.existsById(idPessoa)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pessoa nao encontrada.");
        }
        List<CodigoAnonimo> codigos = new ArrayList<>();
        for (Identidade vinculo : identidadeRepository.findByIdPessoa(idPessoa)) {
            codigos.add(codigoRepository.findById(vinculo.getIdCodigo()).get());
        }
        return ResponseEntity.ok(codigos);
    }

    private String gerarCodigo(Long idProcesso) {
        Random aleatorio = new Random();
        String codigo;
        do {
            codigo = "";
            for (int i = 0; i < 6; i++) {
                codigo = codigo + ALFABETO.charAt(aleatorio.nextInt(ALFABETO.length()));
            }
        } while (codigoRepository.existsByIdProcessoAndCodigo(idProcesso, codigo));
        return codigo;
    }
}
