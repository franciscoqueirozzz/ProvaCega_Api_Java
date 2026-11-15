package br.com.fiap.controller;

import br.com.fiap.model.dto.PessoaDTO;
import br.com.fiap.model.entity.Pessoa;
import br.com.fiap.model.repository.PessoaRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid PessoaDTO dto) {
        try {
            Pessoa pessoa = pessoaRepository.save(new Pessoa(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(pessoa);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar a pessoa (e-mail ou CPF ja cadastrado?).");
        }
    }

    @GetMapping
    public List<Pessoa> consultar() {
        return pessoaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> consultarPorId(@PathVariable Long id) {
        Optional<Pessoa> busca = pessoaRepository.findById(id);
        if (busca.isPresent()) {
            return ResponseEntity.ok(busca.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pessoa nao encontrada.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody @Valid PessoaDTO dto) {
        Optional<Pessoa> busca = pessoaRepository.findById(id);
        if (busca.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pessoa nao encontrada.");
        }
        try {
            Pessoa pessoa = busca.get();
            pessoa.atualizar(dto);
            pessoaRepository.save(pessoa);
            return ResponseEntity.ok("Pessoa atualizada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao atualizar a pessoa (e-mail ou CPF ja cadastrado?).");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!pessoaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pessoa nao encontrada.");
        }
        try {
            pessoaRepository.deleteById(id);
            return ResponseEntity.ok("Pessoa excluida com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Nao foi possivel excluir: a pessoa esta em uso em outros registros.");
        }
    }
}
