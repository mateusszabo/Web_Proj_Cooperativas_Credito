package br.edu.eseg.cooperativas.repository;

import br.edu.eseg.cooperativas.domain.enums.StatusConta;
import br.edu.eseg.cooperativas.domain.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    @Query("SELECT c FROM Conta c JOIN FETCH c.associado a JOIN FETCH a.cooperativa ORDER BY c.idConta DESC")
    List<Conta> listarTodasComAssociadoECooperativa();

    List<Conta> findByAssociadoIdAssociado(Long idAssociado);

    boolean existsByAssociadoIdAssociadoAndStatusConta(Long idAssociado, StatusConta statusConta);

    @Query("SELECT COALESCE(SUM(c.saldoMedioMensal), 0) FROM Conta c WHERE c.statusConta = 'ATIVA'")
    BigDecimal somarSaldoContasAtivas();

    @Query("SELECT COALESCE(SUM(c.saldoMedioMensal), 0) FROM Conta c WHERE c.associado.cooperativa.idCooperativa = :idCoop AND c.statusConta = 'ATIVA'")
    BigDecimal somarDepositosAtivosPorCooperativa(@Param("idCoop") Long idCooperativa);
}