package com.stationhub.io;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stationhub.command.Command;
import com.stationhub.exception.InvalidInputException;
import com.stationhub.response.Response;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class JsonIO {

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private JsonIO() { }

    public static List<Command> readCommands(Path file) {
        try {
            return MAPPER.readValue(file.toFile(), new TypeReference<List<Command>>() { });
        } catch (IOException e) {
            throw new InvalidInputException("Cannot read commands: " + e.getMessage());
        }
    }

    public static void writeResponses(Path file, List<Response> responses) {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), responses);
        } catch (IOException e) {
            throw new InvalidInputException("Cannot write output: " + e.getMessage());
        }
    }
}

