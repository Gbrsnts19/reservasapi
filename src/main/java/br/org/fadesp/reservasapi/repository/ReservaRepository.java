package br.org.fadesp.reservasapi.repository;

import br.org.fadesp.reservasapi.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}