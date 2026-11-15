package br.com.fiap.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import br.com.fiap.model.dto.RespostaDTO;
import br.com.fiap.exception.RegraNegocioException;

/** Entidade da tabela tb_resposta. */
@Entity
@Table(name = "tb_resposta")
public class Resposta {

    @Id
    @SequenceGenerator(name = "seq_resposta", sequenceName = "seq_resposta", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_resposta")
    @Column(name = "id_resposta")
    private Long idResposta;

    @Column(name = "id_codigo")
    private Long idCodigo;

    @Lob
    @Column(name = "conteudo")
    private String conteudo;

    @Column(name = "data_envio")
    private LocalDate dataEnvio;

    @Column(name = "metadados_removidos")
    private String metadadosRemovidos;

    @Column(name = "situacao")
    private String situacao;

    @Column(name = "nota_final")
    private BigDecimal notaFinal;

    @Column(name = "resultado")
    private String resultado;

    @Column(name = "data_fechamento")
    private LocalDate dataFechamento;

    public Resposta() {
    }

    public Resposta(RespostaDTO dto, Long idCodigo) {
        this.idCodigo = idCodigo;
        this.conteudo = dto.conteudo();
        this.dataEnvio = LocalDate.now();
        this.metadadosRemovidos = "S";
        this.situacao = "ENVIADA";
    }

    public void iniciarAvaliacao() throws RegraNegocioException {
        if ("FECHADA".equals(this.situacao)) {
            throw new RegraNegocioException("A resposta ja esta FECHADA.");
        }
        if ("ENVIADA".equals(this.situacao)) {
            this.situacao = "EM_AVALIACAO";
        }
    }

    /** nota final, resultado e data sao preenchidos sempre juntos. Aprovado com nota 7 ou mais. */
    public void fechar(BigDecimal nota) throws RegraNegocioException {
        if ("FECHADA".equals(this.situacao)) {
            throw new RegraNegocioException("A resposta ja esta FECHADA.");
        }
        this.notaFinal = nota;
        this.resultado = nota.compareTo(new BigDecimal("7")) >= 0 ? "APROVADO" : "REPROVADO";
        this.dataFechamento = LocalDate.now();
        this.situacao = "FECHADA";
    }

    public Long getIdResposta() {
        return idResposta;
    }

    public Long getIdCodigo() {
        return idCodigo;
    }

    public void setIdCodigo(Long idCodigo) {
        this.idCodigo = idCodigo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDate getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDate dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getMetadadosRemovidos() {
        return metadadosRemovidos;
    }

    public void setMetadadosRemovidos(String metadadosRemovidos) {
        this.metadadosRemovidos = metadadosRemovidos;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(BigDecimal notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDate getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDate dataFechamento) {
        this.dataFechamento = dataFechamento;
    }
}
