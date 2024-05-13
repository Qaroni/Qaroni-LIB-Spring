package com.qaroni.libs.tests.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@JSONAssertions
class JSONAssertionTest {
    @DisplayName("Assert a json from a file is equals to a json string")
    @Test
    void assertEqualsJSONFromString(JSONAssertion jsonAssertion) {
        String expectedResponsePath = "tests/json/expected.json";
        String response = "{\"first\": \"Hello\", \"second\": \"World\"}";

        assertDoesNotThrow(() -> jsonAssertion.assertEquals(expectedResponsePath, response));
    }

    @DisplayName("Assert a json from a file is equals to a json object")
    @Test
    void assertEqualsJSONFromObject(JSONAssertion jsonAssertion) {
        String expectedResponsePath = "tests/json/expected.json";
        Object response = new Object() {
            public String first = "Hello";
            public String second = "World";
        };

        assertDoesNotThrow(() -> jsonAssertion.assertEquals(expectedResponsePath, response));
    }
}