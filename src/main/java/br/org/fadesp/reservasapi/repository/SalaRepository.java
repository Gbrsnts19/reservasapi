package br.org.fadesp.reservasapi.repository;

import br.org.fadesp.reservasapi.domain.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, Long> {
}