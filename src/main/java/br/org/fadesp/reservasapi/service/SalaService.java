package br.org.fadesp.reservasapi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.org.fadesp.reservasapi.domain.Sala;
import br.org.fadesp.reservasapi.dto.SalaRequest;
import br.org.fadesp.reservasapi.dto.SalaResponse;
import br.org.fadesp.reservasapi.exception.RecursoNaoEncontradoException;
import br.org.fadesp.reservasapi.repository.SalaRepository;

@Service
public class SalaService {

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @Transactional
    public SalaResponse criar(SalaRequest request) {
        Sala sala = new Sala(request.getNome(), request.getTipo(), request.getCapacidade());
        Sala salva = salaRepository.save(sala);
        return SalaResponse.from(salva);
    }

    @Transactional(readOnly = true)
    public List<SalaResponse> listar() {
        return salaRepository.findAll().stream()
                .map(SalaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalaResponse buscarPorId(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada: " + id));
        return SalaResponse.from(sala);
    }
}