package br.org.fadesp.reservasapi.dto;

import br.org.fadesp.reservasapi.domain.Sala;
import br.org.fadesp.reservasapi.domain.TipoSala;

public class SalaResponse {

    private Long id;
    private String nome;
    private TipoSala tipo;
    private Integer capacidade;
    private boolean ativa;

    public SalaResponse(Long id, String nome, TipoSala tipo, Integer capacidade, boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.capacidade = capacidade;
        this.ativa = ativa;
    }

    public static SalaResponse from(Sala sala) {
        return new SalaResponse(
                sala.getId(),
                sala.getNome(),
                sala.getTipo(),
                sala.getCapacidade(),
                sala.isAtiva()
        );
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoSala getTipo() {
        return tipo;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public boolean isAtiva() {
        return ativa;
    }
}
