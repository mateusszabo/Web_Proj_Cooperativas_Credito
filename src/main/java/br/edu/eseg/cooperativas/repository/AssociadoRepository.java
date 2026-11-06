package br.edu.eseg.cooperativas.repository;

import br.edu.eseg.cooperativas.domain.enums.StatusAssociado;
import br.edu.eseg.cooperativas.domain.model.Associado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AssociadoRepository extends JpaRepository<Associado, Long> {

    @Query("SELECT a FROM Associado a JOIN FETCH a.cooperativa ORDER BY a.idAssociado DESC")
    List<Associado> listarTodosComCooperativa();

    List<Associado> findByCooperativaIdCooperativa(Long idCooperativa);

    long countByStatusAssociado(StatusAssociado status);

    @Query("SELECT COUNT(a) FROM Associado a WHERE a.cooperativa.idCooperativa = :idCoop AND a.statusAssociado = 'ATIVO'")
    long contarAssociadosAtivosPorCooperativa(@Param("idCoop") Long idCooperativa);
}