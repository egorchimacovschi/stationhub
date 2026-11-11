# Architecture

## 1. Layers

```
   input.json                                                    output.json
       │                                                              ▲
       ▼                                                              │
┌──────────────┐    ┌────────────────────┐    ┌───────────────────────────┐
│   io layer   │───▶│   command layer    │───▶│      response layer       │
│   JsonIO     │    │ Command (iface)    │    │ Response (abstract)       │
│  TimeFormats │    │ AbstractCommand    │    │ Rejected / Ok / Missing / │
└──────────────┘    │ WriteCommand ...   │    │ Denied                    │
                    └─────────┬──────────┘    └───────────────────────────┘
                              │ uses
          ┌───────────────────┼─────────────────────┐
          ▼                   ▼                     ▼
 ┌─────────────────┐  ┌───────────────┐   ┌───────────────────────┐
 │ SimulationClock │  │ Reading       │   │ ReadingStore (iface)  │
 │ EngineContext   │  │ .validate()   │   │ InMemoryReadingStore  │
 └─────────────────┘  │ Rules / Rule  │   │   └─ DailyLog (48)    │
                      └───────────────┘   └───────────────────────┘
```

Dependencies point downwards: `io` knows commands and responses, commands know the domain and the store interface, the domain knows nothing about commands.

## 2. Class hierarchies

```
Reading (abstract, sealed)              Command (interface)
 ├─ TemperatureReading                   └─ AbstractCommand (template method)
 ├─ TemperatureForecastReading                ├─ StartFeedCommand
 ├─ AirQualityReading                         ├─ StopFeedCommand
 ├─ AirQualityForecastReading                 ├─ QueryCommand
 ├─ WindReading                               └─ WriteCommand (abstract)
 └─ PrecipitationReading                           ├─ PublishCommand
                                                   └─ CorrectCommand
Response (abstract)
 ├─ RejectedReading                     ReadingStore (interface)
 └─ QueryResponse (abstract)             └─ InMemoryReadingStore
      ├─ QueryOk
      ├─ QueryMissing                   Rule<T> (functional interface)
      └─ QueryDenied                     └─ created by static methods in Rules
```

## 3. Sequence: a `publish` command

```
Engine        PublishCommand      Clock     Reading        Store        Context
  │ execute()      │                │          │             │             │
  ├───────────────▶│ advanceTo(ts)  │          │             │             │
  │                ├───────────────▶│          │             │             │
  │                │ isAllowed? (feed on)      │             │             │
  │                ├─────────────────────────────────────────────────────▶│
  │                │ for each reading:         │             │             │
  │                │   previousValid(r) ───────────────────▶│             │
  │                │   r.validate(prev) ──────▶│             │             │
  │                │   invalid ─ emit(RejectedReading) ─────────────────▶ │
  │                │   valid   ─ save(r) ──────────────────▶│             │
```

## 4. Decision flow of a `query`

```
query(tier, dataType, target | from+to)
  │
  ├─ resolve the window and look the readings up
  │     target = LATEST     -> newest stored reading (window = its slot)
  │     target = timestamp  -> the slot that contains it
  │     from + to           -> all stored slots fully inside the range
  │
  ├─ dataType.requireReadableBy(tier)  throws AccessDeniedException ──▶ QueryDenied
  │
  └─ nothing found ──▶ QueryMissing        something found ──▶ QueryOk
```

## 5. One stream, one morning

```
00:00 startFeed                      feed on
00:30 publish  4.2  (00:00 - 00:30)  valid     -> slot 1 stored
01:00 publish  75.0 (00:30 - 01:00)  invalid   -> REJECTED, slot 2 stays empty
01:30 publish  5.0  (01:00 - 01:30)  valid     -> slot 3 stored
01:40 stopFeed                       feed off
02:00 publish  6.0  (01:30 - 02:00)  ignored   (feed is off)
02:10 correct  4.8  (00:30 - 01:00)  valid     -> slot 2 filled retroactively
02:11 query    00:00 .. 02:00        OK with slots 1, 2, 3
```

