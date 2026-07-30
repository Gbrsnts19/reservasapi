package br.org.fadesp.reservasapi.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import br.org.fadesp.reservasapi.domain.Reserva;
import br.org.fadesp.reservasapi.domain.StatusReserva;

public class ReservaResponse {

    private Long id;
    private Long salaId;
    private String salaNome;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String responsavel;
    private StatusReserva status;

    public ReservaResponse(
            Long id,
            Long salaId,
            String salaNome,
            LocalDate data,
            LocalTime horaInicio,
            LocalTime horaFim,
            String responsavel,
            StatusReserva status
    ) {
        this.id = id;
        this.salaId = salaId;
        this.salaNome = salaNome;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.responsavel = responsavel;
        this.status = status;
    }

    public static ReservaResponse from(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getSala().getId(),
                reserva.getSala().getNome(),
                reserva.getData(),
                reserva.getHoraInicio(),
                reserva.getHoraFim(),
                reserva.getResponsavel(),
                reserva.getStatus()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getSalaId() {
        return salaId;
    }

    public String getSalaNome() {
        return salaNome;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public StatusReserva getStatus() {
        return status;
    }
}