package br.com.fiap.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import br.com.fiap.model.dto.ProcessoDTO;
import br.com.fiap.exception.RegraNegocioException;
import br.com.fiap.exception.ProcessoNaoAbertoException;
import java.util.List;

/** Entidade da tabela tb_processo. */
@Entity
@Table(name = "tb_processo")
public class Processo {

    @Id
    @SequenceGenerator(name = "seq_processo", sequenceName = "seq_processo", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_processo")
    @Column(name = "id_processo")
    private Long idProcesso;

    @Column(name = "id_organizador")
    private Long idOrganizador;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "prazo_contestacao")
    private LocalDate prazoContestacao;

    @Column(name = "diferenca_maxima")
    private BigDecimal diferencaMaxima;

    @Column(name = "situacao")
    private String situacao;

    public Processo() {
    }

    public Processo(ProcessoDTO dto) throws RegraNegocioException {
        atualizar(dto);
        this.idOrganizador = dto.idOrganizador();
        this.situacao = "EM_CONFIGURACAO";
    }

    public void atualizar(ProcessoDTO dto) throws RegraNegocioException {
        if (dto.dataFim().isBefore(dto.dataInicio())) {
            throw new RegraNegocioException("A data de fim nao pode ser anterior a data de inicio.");
        }
        this.nome = dto.nome();
        this.descricao = dto.descricao();
        this.dataInicio = dto.dataInicio();
        this.dataFim = dto.dataFim();
        this.prazoContestacao = dto.prazoContestacao();
        this.diferencaMaxima = dto.diferencaMaxima();
    }

    /** O processo avanca uma fase por vez: EM_CONFIGURACAO, ABERTO, EM_AVALIACAO, ENCERRADO. */
    public void avancarPara(String nova) throws RegraNegocioException {
        List<String> fases = List.of("EM_CONFIGURACAO", "ABERTO", "EM_AVALIACAO", "ENCERRADO");
        if (fases.indexOf(nova) != fases.indexOf(this.situacao) + 1) {
            throw new RegraNegocioException("Transicao invalida: " + this.situacao + " para " + nova
                    + ". O processo avanca uma fase por vez.");
        }
        this.situacao = nova;
    }

    public void exigirAberto() throws ProcessoNaoAbertoException {
        if (!"ABERTO".equals(this.situacao)) {
            throw new ProcessoNaoAbertoException("O processo nao esta ABERTO para envio de respostas.");
        }
    }

    public Long getIdProcesso() {
        return idProcesso;
    }

    public Long getIdOrganizador() {
        return idOrganizador;
    }

    public void setIdOrganizador(Long idOrganizador) {
        this.idOrganizador = idOrganizador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public LocalDate getPrazoContestacao() {
        return prazoContestacao;
    }

    public void setPrazoContestacao(LocalDate prazoContestacao) {
        this.prazoContestacao = prazoContestacao;
    }

    public BigDecimal getDiferencaMaxima() {
        return diferencaMaxima;
    }

    public void setDiferencaMaxima(BigDecimal diferencaMaxima) {
        this.diferencaMaxima = diferencaMaxima;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
