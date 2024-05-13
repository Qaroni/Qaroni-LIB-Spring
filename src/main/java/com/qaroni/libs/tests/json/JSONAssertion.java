package com.qaroni.libs.tests.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.mockito.internal.matchers.Any;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JSONAssertion {
    private final ObjectMapper objectMapper;
    private final Path parentPath;

    public JSONAssertion(ObjectMapper objectMapper, Path parentPath) {
        this.objectMapper = objectMapper;
        this.parentPath = parentPath;
    }

    public void assertEquals(String expectedResponsePath, String response) throws IOException {
        JsonNode expectedTree = objectMapper.readTree(readFromFile(expectedResponsePath));
        String expectedJSONPretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedTree);

        JsonNode currentTree = objectMapper.readTree(response);
        String currentJSONPretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currentTree);

        Assertions.assertEquals(expectedJSONPretty, currentJSONPretty);
    }

    void assertEquals(String expectedResponsePath, Object response) throws IOException {
        JsonNode expectedTree = objectMapper.readTree(readFromFile(expectedResponsePath));
        String expectedJSONPretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedTree);

        JsonNode currentTree = objectMapper.valueToTree(response);
        String currentJSONPretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currentTree);

        Assertions.assertEquals(expectedJSONPretty, currentJSONPretty);
    }

    public String readFromFile(String filePath) throws IOException {
        return Files.readString(parentPath.resolve(filePath));
    }
}
