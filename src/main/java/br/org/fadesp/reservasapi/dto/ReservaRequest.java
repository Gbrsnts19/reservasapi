package br.org.fadesp.reservasapi.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "ReservaRequest", description = "Dados para criação ou atualização de reserva")
public class ReservaRequest {

    @NotNull(message = "Sala é obrigatória")
    @Schema(example = "1", description = "Identificador da sala")
    private Long salaId;

    @NotNull(message = "Data é obrigatória")
    @Schema(example = "2026-08-01", description = "Data da reserva (não pode ser no passado)")
    private LocalDate data;

    @NotNull(message = "Hora de início é obrigatória")
    @Schema(example = "09:00", description = "Horário de início")
    private LocalTime horaInicio;

    @NotNull(message = "Hora de fim é obrigatória")
    @Schema(example = "10:00", description = "Horário de fim")
    private LocalTime horaFim;

    @NotBlank(message = "Responsável é obrigatório")
    @Size(max = 120, message = "Responsável deve ter no máximo 120 caracteres")
    @Schema(example = "Gabriel", description = "Nome do responsável pela reserva")
    private String responsavel;

    public Long getSalaId() {
        return salaId;
    }

    public void setSalaId(Long salaId) {
        this.salaId = salaId;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }
}
