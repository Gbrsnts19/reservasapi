package br.org.fadesp.reservasapi.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.org.fadesp.reservasapi.domain.Reserva;
import br.org.fadesp.reservasapi.domain.StatusReserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
            FROM Reserva r
            WHERE r.sala.id = :salaId
              AND r.data = :data
              AND r.status = :status
              AND r.horaInicio < :horaFim
              AND r.horaFim > :horaInicio
            """)
    boolean existsConflito(
            @Param("salaId") Long salaId,
            @Param("data") LocalDate data,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFim") LocalTime horaFim,
            @Param("status") StatusReserva status
    );

    List<Reserva> findByDataOrderByHoraInicioAsc(LocalDate data);

    List<Reserva> findByDataAndStatusOrderByHoraInicioAsc(LocalDate data, StatusReserva status);
}