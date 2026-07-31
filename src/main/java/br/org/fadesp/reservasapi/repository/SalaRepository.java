package br.org.fadesp.reservasapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.org.fadesp.reservasapi.domain.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    List<Sala> findByAtivaTrue();

    Optional<Sala> findByIdAndAtivaTrue(Long id);
}
