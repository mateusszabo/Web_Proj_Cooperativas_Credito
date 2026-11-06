package br.edu.eseg.cooperativas.repository;

import br.edu.eseg.cooperativas.domain.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("SELECT t FROM Transacao t JOIN FETCH t.conta c JOIN FETCH c.associado a JOIN FETCH a.cooperativa ORDER BY t.dataHora DESC")
    List<Transacao> listarTodasComContaEAssociado();

    List<Transacao> findByContaIdContaOrderByDataHoraDesc(Long idConta);
}