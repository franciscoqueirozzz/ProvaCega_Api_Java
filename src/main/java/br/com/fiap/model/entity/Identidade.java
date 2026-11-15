package br.com.fiap.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import br.com.fiap.model.dto.CodigoDTO;

/** Entidade da tabela tb_identidade. */
@Entity
@Table(name = "tb_identidade")
public class Identidade {

    @Id
    @SequenceGenerator(name = "seq_identidade", sequenceName = "seq_identidade", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_identidade")
    @Column(name = "id_vinculo")
    private Long idVinculo;

    @Column(name = "id_codigo")
    private Long idCodigo;

    @Column(name = "id_processo")
    private Long idProcesso;

    @Column(name = "id_pessoa")
    private Long idPessoa;

    @Column(name = "data_vinculo")
    private LocalDate dataVinculo;

    public Identidade() {
    }

    /** Ligação de uma pessoa e o codigo anonimo dela. */
    public Identidade(CodigoDTO dto, Long idCodigo) {
        this.idCodigo = idCodigo;
        this.idProcesso = dto.idProcesso();
        this.idPessoa = dto.idPessoa();
        this.dataVinculo = LocalDate.now();
    }

    public Long getIdVinculo() {
        return idVinculo;
    }

    public Long getIdCodigo() {
        return idCodigo;
    }

    public void setIdCodigo(Long idCodigo) {
        this.idCodigo = idCodigo;
    }

    public Long getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public LocalDate getDataVinculo() {
        return dataVinculo;
    }

    public void setDataVinculo(LocalDate dataVinculo) {
        this.dataVinculo = dataVinculo;
    }
}
