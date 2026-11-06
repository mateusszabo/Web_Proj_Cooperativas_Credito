package br.edu.eseg.cooperativas.repository;

import br.edu.eseg.cooperativas.domain.enums.StatusContrato;
import br.edu.eseg.cooperativas.domain.model.OperacaoCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface OperacaoCreditoRepository extends JpaRepository<OperacaoCredito, Long> {

    @Query("SELECT op FROM OperacaoCredito op JOIN FETCH op.associado a JOIN FETCH a.cooperativa ORDER BY op.idOperacao DESC")
    List<OperacaoCredito> listarTodasComAssociadoECooperativa();

    List<OperacaoCredito> findByAssociadoIdAssociado(Long idAssociado);

    long countByStatusContrato(StatusContrato status);

    @Query("SELECT COALESCE(SUM(op.parcelaMensal), 0) FROM OperacaoCredito op " +
            "WHERE op.associado.idAssociado = :idAssociado AND op.statusContrato IN ('ATIVO', 'EM_ATRASO', 'RENEGOCIADO')")
    BigDecimal somarParcelasAtivasDoAssociado(@Param("idAssociado") Long idAssociado);

    @Query("SELECT COALESCE(SUM(op.valorContratado), 0) FROM OperacaoCredito op " +
            "WHERE op.statusContrato IN ('ATIVO', 'EM_ATRASO', 'RENEGOCIADO')")
    BigDecimal somarCarteiraCreditoAtivaGlobal();

    @Query("SELECT COALESCE(SUM(op.valorContratado), 0) FROM OperacaoCredito op " +
            "WHERE op.associado.cooperativa.idCooperativa = :idCoop AND op.statusContrato IN ('ATIVO', 'EM_ATRASO', 'RENEGOCIADO')")
    BigDecimal somarCarteiraAtivaPorCooperativa(@Param("idCoop") Long idCooperativa);
}