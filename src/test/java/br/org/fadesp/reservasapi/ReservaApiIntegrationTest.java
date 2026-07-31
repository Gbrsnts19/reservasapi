package br.org.fadesp.reservasapi;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import br.org.fadesp.reservasapi.repository.ReservaRepository;
import br.org.fadesp.reservasapi.repository.SalaRepository;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
class ReservaApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    private LocalDate dataFutura;

    @BeforeEach
    void setUp() {
        reservaRepository.deleteAll();
        salaRepository.deleteAll();
        dataFutura = LocalDate.now().plusDays(7);
    }

    @Test
    void deveCadastrarEListarSala() throws Exception {
        criarSala("Sala FADESP", "COLETIVA", 8);

        mockMvc.perform(get("/api/salas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Sala FADESP"));
    }

    @Test
    void deveCriarReservaComSucesso() throws Exception {
        long salaId = criarSala("Sala FADESP", "COLETIVA", 8);

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaJson(salaId, dataFutura, "09:00", "10:00", "Gabriel")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ATIVA"))
                .andExpect(jsonPath("$.responsavel").value("Gabriel"))
                .andExpect(jsonPath("$.salaNome").value("Sala FADESP"));
    }

    @Test
    void deveRetornarConflitoQuandoHorarioSobrepoe() throws Exception {
        long salaId = criarSala("Sala FADESP", "COLETIVA", 8);

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaJson(salaId, dataFutura, "09:00", "10:00", "Gabriel")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaJson(salaId, dataFutura, "09:30", "10:30", "Nicole")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "Já existe uma reserva ativa para esta sala no horário informado"));
    }

    @Test
    void deveCancelarReservaEImpedirCancelamentoDuplicado() throws Exception {
        long salaId = criarSala("Sala FADESP", "COLETIVA", 8);
        long reservaId = criarReserva(salaId, dataFutura, "09:00", "10:00", "Gabriel");

        mockMvc.perform(delete("/api/reservas/{id}", reservaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));

        mockMvc.perform(delete("/api/reservas/{id}", reservaId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Reserva já está cancelada"));
    }

    @Test
    void deveListarApenasSalasLivresNoHorario() throws Exception {
        long salaOcupada = criarSala("Sala FADESP", "COLETIVA", 8);
        criarSala("Auditório Norte", "AUDITORIO", 50);
        criarReserva(salaOcupada, dataFutura, "09:00", "10:00", "Gabriel");

        mockMvc.perform(get("/api/salas/livres")
                        .param("data", dataFutura.toString())
                        .param("inicio", "09:00")
                        .param("fim", "10:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Auditório Norte"));

        mockMvc.perform(get("/api/salas/livres")
                        .param("data", dataFutura.toString())
                        .param("inicio", "10:00")
                        .param("fim", "11:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void deveRejeitarReservaComDataNoPassado() throws Exception {
        long salaId = criarSala("Sala FADESP", "COLETIVA", 8);
        LocalDate dataPassada = LocalDate.now().minusDays(1);

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaJson(salaId, dataPassada, "09:00", "10:00", "Gabriel")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A data da reserva não pode ser no passado"));
    }

    @Test
    void deveBuscarReservaPorIdEFiltrarAgendaPorStatus() throws Exception {
        long salaId = criarSala("Sala FADESP", "COLETIVA", 8);
        long reservaGabriel = criarReserva(salaId, dataFutura, "09:00", "10:00", "Gabriel");
        criarReserva(salaId, dataFutura, "14:00", "15:00", "Nicole");

        mockMvc.perform(get("/api/reservas/{id}", reservaGabriel))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responsavel").value("Gabriel"));

        mockMvc.perform(delete("/api/reservas/{id}", reservaGabriel))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reservas/agenda").param("data", dataFutura.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/reservas/agenda")
                        .param("data", dataFutura.toString())
                        .param("status", "ATIVA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].responsavel").value("Nicole"));

        mockMvc.perform(get("/api/reservas/agenda")
                        .param("data", dataFutura.toString())
                        .param("status", "CANCELADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].responsavel").value("Gabriel"));
    }

    @Test
    void deveExcluirSalaSemReservaAtivaEImpedirNovaReserva() throws Exception {
        long salaId = criarSala("Sala FADESP", "COLETIVA", 8);

        mockMvc.perform(delete("/api/salas/{id}", salaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa").value(false));

        mockMvc.perform(get("/api/salas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/api/salas/{id}", salaId))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaJson(salaId, dataFutura, "09:00", "10:00", "Gabriel")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Não é possível reservar uma sala excluída"));
    }

    @Test
    void naoDeveExcluirSalaComReservaAtiva() throws Exception {
        long salaId = criarSala("Sala FADESP", "COLETIVA", 8);
        criarReserva(salaId, dataFutura, "09:00", "10:00", "Gabriel");

        mockMvc.perform(delete("/api/salas/{id}", salaId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Não é possível excluir uma sala com reservas ativas"));
    }

    @Test
    void deveExcluirSalaAposCancelarReservaAtiva() throws Exception {
        long salaId = criarSala("Sala FADESP", "COLETIVA", 8);
        long reservaId = criarReserva(salaId, dataFutura, "09:00", "10:00", "Gabriel");

        mockMvc.perform(delete("/api/reservas/{id}", reservaId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/salas/{id}", salaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa").value(false));
    }

    private long criarSala(String nome, String tipo, int capacidade) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/salas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "%s",
                                  "tipo": "%s",
                                  "capacidade": %d
                                }
                                """.formatted(nome, tipo, capacidade)))
                .andExpect(status().isCreated())
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }

    private long criarReserva(
            long salaId,
            LocalDate data,
            String inicio,
            String fim,
            String responsavel
    ) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaJson(salaId, data, inicio, fim, responsavel)))
                .andExpect(status().isCreated())
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }

    private String reservaJson(
            long salaId,
            LocalDate data,
            String inicio,
            String fim,
            String responsavel
    ) {
        return """
                {
                  "salaId": %d,
                  "data": "%s",
                  "horaInicio": "%s",
                  "horaFim": "%s",
                  "responsavel": "%s"
                }
                """.formatted(salaId, data, inicio, fim, responsavel);
    }
}
