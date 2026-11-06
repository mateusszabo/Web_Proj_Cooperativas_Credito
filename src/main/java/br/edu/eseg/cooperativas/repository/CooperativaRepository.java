package br.edu.eseg.cooperativas.repository;

import br.edu.eseg.cooperativas.domain.model.Cooperativa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CooperativaRepository extends JpaRepository<Cooperativa, Long> {
    Optional<Cooperativa> findByNomeIgnoreCase(String nome);
    List<Cooperativa> findByEstadoIgnoreCase(String estado);
}