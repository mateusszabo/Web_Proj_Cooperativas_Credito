package br.edu.eseg.cooperativas.repository;

import br.edu.eseg.cooperativas.domain.model.Investimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface InvestimentoRepository extends JpaRepository<Investimento, Long> {

    @Query("SELECT i FROM Investimento i JOIN FETCH i.associado a JOIN FETCH a.cooperativa ORDER BY i.idInvestimento DESC")
    List<Investimento> listarTodosComAssociado();

    @Query("SELECT COALESCE(SUM(i.valorAplicado), 0) FROM Investimento i WHERE i.statusInvestimento = 'ATIVO'")
    BigDecimal somarInvestimentosAtivos();
}