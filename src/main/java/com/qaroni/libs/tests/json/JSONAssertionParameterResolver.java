package com.qaroni.libs.tests.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.nio.file.Path;

public class JSONAssertionParameterResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(JSONAssertion.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        JSONAssertions JsonAssertions =
                AnnotationSupport.findAnnotation(extensionContext.getRequiredTestClass(), JSONAssertions.class)
                        .orElseThrow();

        return build(JsonAssertions, extensionContext);
    }

    public JSONAssertion build(JSONAssertions JsonAssertions, ExtensionContext extensionContext) {
        ExtensionContext.Store store = extensionContext.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);

        return store.getOrComputeIfAbsent(JsonAssertions, this::build, JSONAssertion.class);
    }

    private JSONAssertion build(JSONAssertions JsonAssertions) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);

        return new JSONAssertion(objectMapper, Path.of(JsonAssertions.basePath()));
    }
}
