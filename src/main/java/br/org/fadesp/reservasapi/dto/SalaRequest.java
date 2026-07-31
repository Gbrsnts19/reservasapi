package br.org.fadesp.reservasapi.dto;

import br.org.fadesp.reservasapi.domain.TipoSala;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "SalaRequest", description = "Dados para cadastro ou atualização de sala")
public class SalaRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(example = "Sala FADESP", description = "Nome da sala")
    private String nome;

    @NotNull(message = "Tipo é obrigatório")
    @Schema(example = "COLETIVA", description = "Tipo da sala")
    private TipoSala tipo;

    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser no mínimo 1")
    @Schema(example = "8", description = "Capacidade máxima")
    private Integer capacidade;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoSala getTipo() {
        return tipo;
    }

    public void setTipo(TipoSala tipo) {
        this.tipo = tipo;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }
}
