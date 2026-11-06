package br.edu.eseg.cooperativas.repository;

import br.edu.eseg.cooperativas.domain.model.RegistroInadimplencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface RegistroInadimplenciaRepository extends JpaRepository<RegistroInadimplencia, Long> {

    boolean existsByOperacaoCreditoIdOperacaoAndMesReferencia(Long idOperacao, String mesReferencia);

    List<RegistroInadimplencia> findByOperacaoCreditoIdOperacaoOrderByMesReferenciaDesc(Long idOperacao);

    @Query("SELECT COALESCE(SUM(r.provisaoPerdaCredito), 0) FROM RegistroInadimplencia r")
    BigDecimal somarProvisaoPerdaTotal();
}