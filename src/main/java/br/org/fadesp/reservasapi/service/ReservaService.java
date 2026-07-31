package br.org.fadesp.reservasapi.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.org.fadesp.reservasapi.domain.Reserva;
import br.org.fadesp.reservasapi.domain.Sala;
import br.org.fadesp.reservasapi.domain.StatusReserva;
import br.org.fadesp.reservasapi.dto.ReservaRequest;
import br.org.fadesp.reservasapi.dto.ReservaResponse;
import br.org.fadesp.reservasapi.exception.ConflitoHorarioException;
import br.org.fadesp.reservasapi.exception.RecursoNaoEncontradoException;
import br.org.fadesp.reservasapi.exception.RegraNegocioException;
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
        validarHorario(request);
        Sala sala = buscarSala(request.getSalaId());
        validarConflito(sala.getId(), request, null);

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
    public ReservaResponse atualizar(Long id, ReservaRequest request) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada: " + id));

        if (reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new RegraNegocioException("Não é possível editar uma reserva cancelada");
        }

        validarHorario(request);
        Sala sala = buscarSala(request.getSalaId());
        validarConflito(sala.getId(), request, id);

        reserva.setSala(sala);
        reserva.setData(request.getData());
        reserva.setHoraInicio(request.getHoraInicio());
        reserva.setHoraFim(request.getHoraFim());
        reserva.setResponsavel(request.getResponsavel());

        return ReservaResponse.from(reserva);
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

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarAgenda(LocalDate data, StatusReserva status) {
        List<Reserva> reservas = status == null
                ? reservaRepository.findByDataOrderByHoraInicioAsc(data)
                : reservaRepository.findByDataAndStatusOrderByHoraInicioAsc(data, status);

        return reservas.stream()
                .map(ReservaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse buscarPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada: " + id));
        return ReservaResponse.from(reserva);
    }

    private void validarHorario(ReservaRequest request) {
        if (!request.getHoraFim().isAfter(request.getHoraInicio())) {
            throw new RegraNegocioException("A hora de fim deve ser posterior à hora de início");
        }
    }

    private Sala buscarSala(Long salaId) {
        return salaRepository.findById(salaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada: " + salaId));
    }

    private void validarConflito(Long salaId, ReservaRequest request, Long reservaIdIgnorada) {
        boolean temConflito = reservaRepository.existsConflito(
                salaId,
                request.getData(),
                request.getHoraInicio(),
                request.getHoraFim(),
                StatusReserva.ATIVA,
                reservaIdIgnorada
        );

        if (temConflito) {
            throw new ConflitoHorarioException(
                    "Já existe uma reserva ativa para esta sala no horário informado");
        }
    }
}
