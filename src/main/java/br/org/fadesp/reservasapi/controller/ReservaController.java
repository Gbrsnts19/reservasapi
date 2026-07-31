package br.org.fadesp.reservasapi.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.org.fadesp.reservasapi.domain.StatusReserva;
import br.org.fadesp.reservasapi.dto.ReservaRequest;
import br.org.fadesp.reservasapi.dto.ReservaResponse;
import br.org.fadesp.reservasapi.service.ReservaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Criação, consulta, atualização e cancelamento de reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse criar(@Valid @RequestBody ReservaRequest request) {
        return reservaService.criar(request);
    }

    @GetMapping("/agenda")
    public List<ReservaResponse> listarAgenda(
            @Parameter(description = "Data da agenda", example = "2026-08-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @Parameter(description = "Filtro opcional de status (ATIVA ou CANCELADA)", example = "ATIVA")
            @RequestParam(required = false) StatusReserva status
    ) {
        return reservaService.listarAgenda(data, status);
    }

    @GetMapping("/{id}")
    public ReservaResponse buscarPorId(
            @Parameter(description = "ID da reserva", example = "1")
            @PathVariable Long id
    ) {
        return reservaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ReservaResponse atualizar(
            @Parameter(description = "ID da reserva", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequest request
    ) {
        return reservaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ReservaResponse cancelar(
            @Parameter(description = "ID da reserva", example = "1")
            @PathVariable Long id
    ) {
        return reservaService.cancelar(id);
    }
}