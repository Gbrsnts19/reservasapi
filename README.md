# API de Reservas - Coworking

API REST para centralizar o gerenciamento de salas e reservas de uma empresa de coworking e eventos empresariais.

Desenvolvida como desafio técnico (FADESP).

## Stack

- Java 17
- Spring Boot 4.1.0
- Maven
- Spring Web / Spring Data JPA / Bean Validation
- Banco H2 (embutido, em memória)
- springdoc-openapi (Swagger UI)

## Requisitos

- JDK 17+
- Maven Wrapper incluso (`./mvnw`) — não é necessário instalar Maven globalmente

## Como executar

```bash
./mvnw spring-boot:run
```

A API sobe em: [http://localhost:8080](http://localhost:8080)

### Endereços úteis

| Recurso | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| H2 Console | http://localhost:8080/h2-console |

### H2 Console

- JDBC URL: `jdbc:h2:mem:reservasdb`
- User: `sa`
- Password: *(vazio)*

> O H2 está em memória: ao reiniciar a aplicação, os dados são apagados.

## Como rodar os testes

```bash
./mvnw test
```

## Endpoints

### Salas

| Método | Caminho | Descrição |
|--------|---------|-----------|
| POST | `/api/salas` | Cadastrar sala |
| GET | `/api/salas` | Listar salas |
| GET | `/api/salas/{id}` | Buscar sala por id |
| PUT | `/api/salas/{id}` | Atualizar sala |
| GET | `/api/salas/livres` | Listar salas livres em um dia/horário |

### Reservas

| Método | Caminho | Descrição |
|--------|---------|-----------|
| POST | `/api/reservas` | Criar reserva |
| GET | `/api/reservas/{id}` | Buscar reserva por id |
| PUT | `/api/reservas/{id}` | Atualizar reserva |
| DELETE | `/api/reservas/{id}` | Cancelar reserva (status `CANCELADA`) |
| GET | `/api/reservas/agenda` | Agenda diária (`status` opcional) |

### Tipos de sala

`COLETIVA` | `INDIVIDUAL` | `AUDITORIO`

### Status de reserva

`ATIVA` | `CANCELADA`

## Regras de negócio principais

- Não permite overlap de reservas **ATIVAS** na mesma sala/dia
- Horários encostados são permitidos (ex.: 09:00–10:00 e 10:00–11:00)
- Cancelamento é lógico (não apaga o registro)
- Reserva cancelada não bloqueia novo agendamento no mesmo horário
- Não é possível editar reserva cancelada
- Data da reserva não pode ser no passado
- Hora de fim deve ser posterior à hora de início

## Exemplos de uso (curl)

> Use uma **data futura** nos exemplos (abaixo: `2026-08-01`).  
> Com a API reiniciada, execute na ordem.

### 1. Criar salas

```bash
curl -s -X POST http://localhost:8080/api/salas \
  -H "Content-Type: application/json" \
  -d '{"nome":"Sala FADESP","tipo":"COLETIVA","capacidade":8}'

curl -s -X POST http://localhost:8080/api/salas \
  -H "Content-Type: application/json" \
  -d '{"nome":"Auditório Norte","tipo":"AUDITORIO","capacidade":50}'
```

### 2. Listar e buscar sala

```bash
curl -s http://localhost:8080/api/salas

curl -s http://localhost:8080/api/salas/1
```

### 3. Atualizar sala

```bash
curl -s -X PUT http://localhost:8080/api/salas/1 \
  -H "Content-Type: application/json" \
  -d '{"nome":"Sala FADESP Premium","tipo":"INDIVIDUAL","capacidade":4}'
```

### 4. Criar reserva

```bash
curl -s -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "salaId": 1,
    "data": "2026-08-01",
    "horaInicio": "09:00",
    "horaFim": "10:00",
    "responsavel": "Gabriel"
  }'
```

### 5. Horário inválido (400)

```bash
curl -s -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "salaId": 1,
    "data": "2026-08-01",
    "horaInicio": "10:00",
    "horaFim": "09:00",
    "responsavel": "Gabriel"
  }'
```

### 6. Data no passado (400)

```bash
curl -s -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "salaId": 1,
    "data": "2020-01-15",
    "horaInicio": "09:00",
    "horaFim": "10:00",
    "responsavel": "Gabriel"
  }'
```

### 7. Conflito de horário (409)

```bash
curl -s -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "salaId": 1,
    "data": "2026-08-01",
    "horaInicio": "09:30",
    "horaFim": "10:30",
    "responsavel": "Nicole"
  }'
```

### 8. Horário encostado (ok)

```bash
curl -s -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "salaId": 1,
    "data": "2026-08-01",
    "horaInicio": "10:00",
    "horaFim": "11:00",
    "responsavel": "Nicole"
  }'
```

### 9. Buscar reserva por id

```bash
curl -s http://localhost:8080/api/reservas/1
```

### 10. Atualizar reserva

```bash
# ok — muda para 11:00-12:00
curl -s -X PUT http://localhost:8080/api/reservas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "salaId": 1,
    "data": "2026-08-01",
    "horaInicio": "11:00",
    "horaFim": "12:00",
    "responsavel": "Gabriel"
  }'

# 409 — conflito com a reserva da Nicole (10:00-11:00), se ainda ativa no mesmo slot
curl -s -X PUT http://localhost:8080/api/reservas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "salaId": 1,
    "data": "2026-08-01",
    "horaInicio": "10:00",
    "horaFim": "11:00",
    "responsavel": "Gabriel"
  }'
```

### 11. Cancelar reserva

```bash
curl -s -X DELETE http://localhost:8080/api/reservas/1

# cancelar de novo → 400
curl -s -X DELETE http://localhost:8080/api/reservas/1

# remarcar o mesmo horário após cancelamento → ok
curl -s -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "salaId": 1,
    "data": "2026-08-01",
    "horaInicio": "11:00",
    "horaFim": "12:00",
    "responsavel": "Gabriel"
  }'
```

### 12. Agenda diária

```bash
# todas
curl -s "http://localhost:8080/api/reservas/agenda?data=2026-08-01"

# só ativas
curl -s "http://localhost:8080/api/reservas/agenda?data=2026-08-01&status=ATIVA"

# só canceladas
curl -s "http://localhost:8080/api/reservas/agenda?data=2026-08-01&status=CANCELADA"
```

### 13. Salas livres

```bash
# horário ocupado pela Sala FADESP (se houver reserva ativa 09:00-10:00)
curl -s "http://localhost:8080/api/salas/livres?data=2026-08-01&inicio=09:00&fim=10:00"

# horário livre
curl -s "http://localhost:8080/api/salas/livres?data=2026-08-01&inicio=15:00&fim=16:00"

# horário inválido → 400
curl -s "http://localhost:8080/api/salas/livres?data=2026-08-01&inicio=10:00&fim=09:00"
```

## Estrutura do projeto

```text
src/main/java/br/org/fadesp/reservasapi/
├── config/          # OpenAPI/Swagger
├── controller/      # Endpoints REST
├── domain/          # Entidades e enums
├── dto/             # Request/Response
├── exception/       # Handler e exceções de negócio
├── repository/      # Spring Data JPA
└── service/         # Regras de negócio
```

## Autor

Gabriel de Oliveira Carvalho Santos
