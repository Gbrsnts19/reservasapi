package br.org.fadesp.reservasapi.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.org.fadesp.reservasapi.domain.Sala;
import br.org.fadesp.reservasapi.domain.StatusReserva;
import br.org.fadesp.reservasapi.dto.SalaRequest;
import br.org.fadesp.reservasapi.dto.SalaResponse;
import br.org.fadesp.reservasapi.exception.RecursoNaoEncontradoException;
import br.org.fadesp.reservasapi.exception.RegraNegocioException;
import br.org.fadesp.reservasapi.repository.ReservaRepository;
import br.org.fadesp.reservasapi.repository.SalaRepository;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final ReservaRepository reservaRepository;

    public SalaService(SalaRepository salaRepository, ReservaRepository reservaRepository) {
        this.salaRepository = salaRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional
    public SalaResponse criar(SalaRequest request) {
        Sala sala = new Sala(request.getNome(), request.getTipo(), request.getCapacidade());
        Sala salva = salaRepository.save(sala);
        return SalaResponse.from(salva);
    }

    @Transactional
    public SalaResponse atualizar(Long id, SalaRequest request) {
        Sala sala = buscarSalaAtiva(id);

        sala.setNome(request.getNome());
        sala.setTipo(request.getTipo());
        sala.setCapacidade(request.getCapacidade());

        return SalaResponse.from(sala);
    }

    @Transactional
    public SalaResponse excluir(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada: " + id));

        if (!sala.isAtiva()) {
            throw new RegraNegocioException("Sala já está excluída");
        }

        if (reservaRepository.existsBySalaIdAndStatus(id, StatusReserva.ATIVA)) {
            throw new RegraNegocioException(
                    "Não é possível excluir uma sala com reservas ativas");
        }

        sala.setAtiva(false);
        return SalaResponse.from(sala);
    }

    @Transactional(readOnly = true)
    public List<SalaResponse> listar() {
        return salaRepository.findByAtivaTrue().stream()
                .map(SalaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SalaResponse> listarLivres(LocalDate data, LocalTime inicio, LocalTime fim) {
        if (!fim.isAfter(inicio)) {
            throw new RegraNegocioException("A hora de fim deve ser posterior à hora de início");
        }

        return salaRepository.findByAtivaTrue().stream()
                .filter(sala -> !reservaRepository.existsConflito(
                        sala.getId(),
                        data,
                        inicio,
                        fim,
                        StatusReserva.ATIVA,
                        null
                ))
                .map(SalaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalaResponse buscarPorId(Long id) {
        return SalaResponse.from(buscarSalaAtiva(id));
    }

    private Sala buscarSalaAtiva(Long id) {
        return salaRepository.findByIdAndAtivaTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada: " + id));
    }
}
