package br.org.fadesp.reservasapi.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.org.fadesp.reservasapi.dto.SalaRequest;
import br.org.fadesp.reservasapi.dto.SalaResponse;
import br.org.fadesp.reservasapi.service.SalaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalaResponse criar(@Valid @RequestBody SalaRequest request) {
        return salaService.criar(request);
    }

    @GetMapping
    public List<SalaResponse> listar() {
        return salaService.listar();
    }

    @GetMapping("/livres")
    public List<SalaResponse> listarLivres(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime fim
    ) {
        return salaService.listarLivres(data, inicio, fim);
    }

    @GetMapping("/{id}")
    public SalaResponse buscarPorId(@PathVariable Long id) {
        return salaService.buscarPorId(id);
    }
}
