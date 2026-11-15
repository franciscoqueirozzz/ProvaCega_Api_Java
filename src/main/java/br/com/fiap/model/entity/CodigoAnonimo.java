package br.com.fiap.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import br.com.fiap.model.dto.CodigoDTO;
import br.com.fiap.exception.CodigoInvalidoException;

/** Entidade da tabela tb_codigo_anonimo. */
@Entity
@Table(name = "tb_codigo_anonimo")
public class CodigoAnonimo {

    @Id
    @SequenceGenerator(name = "seq_codigo_anonimo", sequenceName = "seq_codigo_anonimo", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_codigo_anonimo")
    @Column(name = "id_codigo")
    private Long idCodigo;

    @Column(name = "id_processo")
    private Long idProcesso;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "situacao")
    private String situacao;

    public CodigoAnonimo() {
    }

    public CodigoAnonimo(CodigoDTO dto, String codigo) {
        this.idProcesso = dto.idProcesso();
        this.codigo = codigo;
        this.dataEmissao = LocalDate.now();
        this.situacao = "ATIVO";
    }

    public void exigirAtivo() throws CodigoInvalidoException {
        if (!"ATIVO".equals(this.situacao)) {
            throw new CodigoInvalidoException("O codigo nao esta ATIVO.");
        }
    }

    public void expirar() {
        this.situacao = "EXPIRADO";
    }

    public Long getIdCodigo() {
        return idCodigo;
    }

    public Long getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
