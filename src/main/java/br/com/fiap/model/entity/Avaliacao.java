package br.com.fiap.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import br.com.fiap.model.dto.AvaliacaoDTO;
import br.com.fiap.model.dto.NotaDTO;
import br.com.fiap.exception.AvaliacaoConcluidaException;
import br.com.fiap.exception.RegraNegocioException;

/** Entidade da tabela tb_avaliacao. */
@Entity
@Table(name = "tb_avaliacao")
public class Avaliacao {

    @Id
    @SequenceGenerator(name = "seq_avaliacao", sequenceName = "seq_avaliacao", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_avaliacao")
    @Column(name = "id_avaliacao")
    private Long idAvaliacao;

    @Column(name = "id_resposta")
    private Long idResposta;

    @Column(name = "id_avaliador")
    private Long idAvaliador;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "nota")
    private BigDecimal nota;

    @Column(name = "parecer")
    private String parecer;

    @Column(name = "data_atribuicao")
    private LocalDate dataAtribuicao;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Column(name = "situacao")
    private String situacao;

    public Avaliacao() {
    }

    public Avaliacao(AvaliacaoDTO dto) {
        this.idResposta = dto.idResposta();
        this.idAvaliador = dto.idAvaliador();
        this.tipo = "NORMAL";
        this.dataAtribuicao = LocalDate.now();
        this.situacao = "PENDENTE";
    }

    public void exigirPendente() throws AvaliacaoConcluidaException {
        if (!"PENDENTE".equals(this.situacao)) {
            throw new AvaliacaoConcluidaException("A avaliacao " + idAvaliacao + " ja foi concluida.");
        }
    }

    public void exigirConcluida() throws RegraNegocioException {
        if ("PENDENTE".equals(this.situacao)) {
            throw new RegraNegocioException("Ainda existem avaliacoes PENDENTE para esta resposta.");
        }
    }

    public void concluir(NotaDTO dto) throws AvaliacaoConcluidaException {
        exigirPendente();
        this.nota = dto.nota();
        this.parecer = dto.parecer();
        this.dataConclusao = LocalDate.now();
        this.situacao = "CONCLUIDA";
    }

    public Long getIdAvaliacao() {
        return idAvaliacao;
    }

    public Long getIdResposta() {
        return idResposta;
    }

    public void setIdResposta(Long idResposta) {
        this.idResposta = idResposta;
    }

    public Long getIdAvaliador() {
        return idAvaliador;
    }

    public void setIdAvaliador(Long idAvaliador) {
        this.idAvaliador = idAvaliador;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public LocalDate getDataAtribuicao() {
        return dataAtribuicao;
    }

    public void setDataAtribuicao(LocalDate dataAtribuicao) {
        this.dataAtribuicao = dataAtribuicao;
    }

    public LocalDate getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDate dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
