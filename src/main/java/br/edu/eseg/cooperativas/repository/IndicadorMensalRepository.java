package br.edu.eseg.cooperativas.repository;

import br.edu.eseg.cooperativas.domain.model.IndicadorMensal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IndicadorMensalRepository extends JpaRepository<IndicadorMensal, Long> {
    List<IndicadorMensal> findByCooperativaIdCooperativaOrderByMesReferenciaDesc(Long idCooperativa);
    boolean existsByCooperativaIdCooperativaAndMesReferencia(Long idCooperativa, String mesReferencia);
}