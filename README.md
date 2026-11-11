# StationHub

Simulated weather-station telemetry hub in plain Java: timed ingestion, self-validating readings, retroactive corrections and tiered queries. No frameworks, only the JDK and the Jackson JSON library.

## What it does

StationHub reads a JSON file of timestamped commands, advances a simulated clock, stores 30-minute readings from six weather streams in memory (rejecting bad ones with a clear reason), and answers queries according to the client's plan (`BASIC` or `PRO`). The output is one JSON array of responses.

```
java -jar target/stationhub-1.0.0.jar  input.json  output.json
```

## Build and run

Requirements: JDK 17+, Maven 3.9+.

```
mvn clean package            # compiles, runs the tests, builds target/stationhub-1.0.0.jar
mvn clean package -DskipTests
java -jar target/stationhub-1.0.0.jar docs/samples/basic.input.json out.json
```

## Time model

- A day has **48 slots** of 30 minutes. Slot 1 = `00:00 - 00:30`, slot 48 = `23:30 - 00:00`.
- 1 tick = 1 simulated minute. Every command has a `timestamp`; the clock moves to it before the command runs.
- Commands with the same timestamp run in file order.
- `publish` only counts while the feed is on (`startFeed` ... `stopFeed`). `correct` and `query` always work.

## Streams

| Stream | Fields | A reading is valid when | PRO only |
|---|---|---|---|
| `TEMPERATURE` | celsius | in [-60, 60] | |
| `TEMPERATURE_FORECAST` | celsius, confidence | celsius in [-60, 60]; confidence in [0, 1] | yes |
| `AIR_QUALITY` | pm25 | >= 0; differs from the previous valid reading by at most 250 | |
| `AIR_QUALITY_FORECAST` | pm25, alertLevel | pm25 >= 0; alertLevel > 0 | |
| `WIND` | speedKmh, gustKmh | both in [0, 400] | |
| `PRECIPITATION` | rainMm, snowMm | both >= 0 | |

Every field must be present. Boundary values are valid. If several fields fail, only the first failing field (in the order above) is reported. "Previous valid reading" means the nearest earlier stored reading of the same stream, even on the previous day.

## Commands

| Command | Payload | Effect | Output |
|---|---|---|---|
| `startFeed` | none | turns the feed on | none |
| `stopFeed` | none | turns the feed off | none |
| `publish` | `readings[]` | validates and stores readings (only while the feed is on) | one `REJECTED` per bad reading |
| `correct` | `readings[]` | same, but always allowed; replaces what is in the slot | one `REJECTED` per bad reading |
| `query` | `tier`, `dataType`, and `target` **or** `from` + `to` | reads data | exactly one response |

```json
{"command":"publish","timestamp":"2026-03-10T00:30","payload":{"readings":[
  {"stream":"TEMPERATURE","date":"2026-03-10","interval":"00:00 - 00:30","celsius":4.2}]}}

{"command":"query","timestamp":"2026-03-10T00:33","payload":{
  "tier":"BASIC","dataType":"TEMPERATURE","target":"2026-03-10T00:00"}}
```

`target` is `"LATEST"` (the newest stored reading) or one timestamp (the slot that contains it). With `from` + `to`, every stored slot that lies fully inside the range is returned, grouped by day.

## Output

A rejected reading:

```json
{"command":"publish","timestamp":"2026-03-10T01:00","status":"REJECTED","stream":"TEMPERATURE",
 "date":"2026-03-10","interval":"00:30 - 01:00","slot":2,
 "reason":"Celsius 75.0 is outside the allowed range [-60, 60]; reading skipped."}
```

Query responses (`from` and `to` are the window that was looked at; for `LATEST` with nothing stored they equal the query timestamp):

| status | meaning | extra fields |
|---|---|---|
| `OK` | at least one reading found | `days: [{stream, date, readings: [{interval, slot, ...values}]}]` |
| `MISSING` | nothing found | `days: null`, `reason: "No reading yet. Try again later."` |
| `DENIED` | `BASIC` client asked for a PRO-only stream | no `days` key, `reason: "Access denied: TEMPERATURE_FORECAST requires a PRO plan."` |

Commands that succeed silently add nothing to the output (no `null`, no `{}`).

Rejection reasons use exactly these patterns (value printed as Java prints a `Double`):

- `{Field} is missing; reading skipped.`
- `{Field} {value} is outside the allowed range [{min}, {max}]; reading skipped.`
- `{Field} {value} is below the minimum {min}; reading skipped.`
- `{Field} {value} must be positive; reading skipped.`
- `Pm25 jumped by more than 250 compared with the previous reading; reading skipped.`

Field names are `Celsius`, `Confidence`, `Pm25`, `AlertLevel`, `SpeedKmh`, `GustKmh`, `RainMm`, `SnowMm`.

## Architecture

```
 input.json ──> JsonIO ──> Command objects ──> Engine loop ──> responses ──> JsonIO ──> output.json
                                 │                  │
                                 │          EngineContext
                                 │     (clock, store, feed flag, output)
                                 ▼
               Reading.validate()  ──>  ReadingStore (InMemoryReadingStore)
                                              └─ DailyLog (48 slots) per stream and day
```

More diagrams (class hierarchies, sequences, query flow) are in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Design decisions (OOP)

| Concept | Where | Why |
|---|---|---|
| Abstraction | `Reading`, `Response`, `AbstractCommand` | the engine works with the abstract type and never needs to know which of the six streams it handles |
| Inheritance | six `Reading` subclasses; `AbstractCommand` -> `WriteCommand` -> `PublishCommand` / `CorrectCommand`; `Response` -> `QueryResponse` -> `QueryOk` / `QueryMissing` / `QueryDenied` | shared behaviour lives once in the parent |
| Polymorphism | `reading.validate()`, `command.execute()`, JSON serialisation of responses | no `if (type == ...)` chains anywhere |
| Encapsulation | private final fields, `DailyLog` hands out only unmodifiable lists | state changes only through a few methods |
| Immutability | readings never change; a correction replaces the reading in its slot | easy to reason about, safe to share |
| Template Method | `AbstractCommand.execute` (clock first, then `doExecute`), `WriteCommand.isAllowed` hook | `publish` and `correct` differ in one line |
| Command pattern | one class per command | each command owns its behaviour |
| Repository | `ReadingStore` interface + `InMemoryReadingStore` | storage can be swapped without touching commands |
| Generics / lambdas | `Rule<T>` and the factories in `Rules` | rules are small reusable values |
| Exceptions | `AccessDeniedException`, `InvalidInputException`; a bad reading is a *result*, not an exception | exceptions only for things that should not happen in normal flow, or that stop the flow (access) |
| Static | `Rules`, `Interval.parse`, constants | stateless helpers |

_Add here: what you tried first, what you changed and why._

## Project layout

```
src/main/java/com/stationhub/
  Main.java
  app/        Engine, EngineContext
  time/       Interval, SimulationClock
  domain/     Reading + 6 subclasses, DailyLog, DataType, ClientTier
  rules/      Rule, Rules
  storage/    ReadingStore, InMemoryReadingStore
  command/    Command, AbstractCommand, WriteCommand, StartFeed/StopFeed/Publish/Correct/QueryCommand
  response/   Response, RejectedReading, QueryResponse, QueryOk, QueryMissing, QueryDenied, DayResult
  io/         JsonIO, TimeFormats
  exception/  InvalidInputException, AccessDeniedException
src/test/java/...   unit tests + a scenario test
docs/               ARCHITECTURE.md, TUTORIAL.md, samples/
```

## Tests

```
mvn test
```

- `IntervalTest`, `ReadingValidationTest`, `InMemoryReadingStoreTest`: unit tests with boundary values.
- `ScenarioTest`: runs the engine on `docs/samples/basic.input.json` and compares with `basic.expected.json`.

## Roadmap

- [ ] Intervals, enums, rules
- [ ] Reading hierarchy, daily log, store
- [ ] Responses, clock, commands
- [ ] JSON in/out, engine, scenario test
- [ ] Stretch goals (see `docs/TUTORIAL.md`)


