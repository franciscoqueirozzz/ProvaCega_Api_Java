package br.com.fiap.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import br.com.fiap.model.dto.PessoaDTO;
import br.com.fiap.exception.PapelInvalidoException;

/** Entidade da tabela tb_pessoa. */
@Entity
@Table(name = "tb_pessoa")
public class Pessoa {

    @Id
    @SequenceGenerator(name = "seq_pessoa", sequenceName = "seq_pessoa", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_pessoa")
    @Column(name = "id_pessoa")
    private Long idPessoa;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "papel")
    private String papel;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    public Pessoa() {
    }

    public Pessoa(PessoaDTO dto) {
        atualizar(dto);
        this.dataCadastro = LocalDate.now();
    }

    public void atualizar(PessoaDTO dto) {
        this.nome = dto.nome();
        this.email = dto.email();
        this.cpf = dto.cpf();
        this.papel = dto.papel();
    }

    public void exigirPapel(String papelExigido) throws PapelInvalidoException {
        if (!papelExigido.equals(this.papel)) {
            throw new PapelInvalidoException("A pessoa " + idPessoa + " precisa ter o papel " + papelExigido + ".");
        }
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
