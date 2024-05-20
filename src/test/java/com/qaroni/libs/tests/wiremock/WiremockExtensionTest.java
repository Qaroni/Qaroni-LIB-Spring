package com.qaroni.libs.tests.wiremock;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WiremockExtensionTest {
    @Wiremock(stubs = {"tests/wiremock/mock.json"})
    @DisplayName("Send a request to a mock endpoint")
    @Test
    void requestToMock() {
        String response = RestAssured.given()
                .contentType("application/json")
                .port(8080)
                .body("{\"data\": \"value\"}")
                .when()
                .post("/endpoint")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .asString();

        assertEquals("{\"response\":\"ok\"}", response);
    }
}