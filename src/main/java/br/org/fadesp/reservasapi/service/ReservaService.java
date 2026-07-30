package br.org.fadesp.reservasapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.org.fadesp.reservasapi.domain.Reserva;
import br.org.fadesp.reservasapi.domain.Sala;
import br.org.fadesp.reservasapi.domain.StatusReserva;
import br.org.fadesp.reservasapi.dto.ReservaRequest;
import br.org.fadesp.reservasapi.dto.ReservaResponse;
import br.org.fadesp.reservasapi.exception.RecursoNaoEncontradoException;
import br.org.fadesp.reservasapi.exception.RegraNegocioException;
import br.org.fadesp.reservasapi.exception.ConflitoHorarioException;
import br.org.fadesp.reservasapi.repository.ReservaRepository;
import br.org.fadesp.reservasapi.repository.SalaRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;

    public ReservaService(ReservaRepository reservaRepository, SalaRepository salaRepository) {
        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
    }

    @Transactional
    public ReservaResponse criar(ReservaRequest request) {
        if (!request.getHoraFim().isAfter(request.getHoraInicio())) {
            throw new RegraNegocioException("A hora de fim deve ser posterior à hora de início");
        }

        Sala sala = salaRepository.findById(request.getSalaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Sala não encontrada: " + request.getSalaId()));

        boolean temConflito = reservaRepository.existsConflito(
                sala.getId(),
                request.getData(),
                request.getHoraInicio(),
                request.getHoraFim(),
                StatusReserva.ATIVA
        );

        if (temConflito) {
            throw new ConflitoHorarioException(
                    "Já existe uma reserva ativa para esta sala no horário informado");
        }

        Reserva reserva = new Reserva(
                sala,
                request.getData(),
                request.getHoraInicio(),
                request.getHoraFim(),
                request.getResponsavel()
        );

        Reserva salva = reservaRepository.save(reserva);
        return ReservaResponse.from(salva);
    }

    @Transactional
    public ReservaResponse cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada: " + id));

        if (reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new RegraNegocioException("Reserva já está cancelada");
        }

        reserva.setStatus(StatusReserva.CANCELADA);
        return ReservaResponse.from(reserva);
    }
}