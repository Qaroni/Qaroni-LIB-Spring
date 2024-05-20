package com.qaroni.libs.tests.wiremock;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

public class WiremockExtension  implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback, AfterEachCallback, ParameterResolver {

    private final Logger logger = Logger.getLogger(WiremockExtension.class.getName());

    private final  ExtensionContext.Namespace WIREMOCK = create("com.qaroni.demoitests.support.wiremock");

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        String testId = extensionContext.getRequiredTestClass().getName();
        resetAll(testId, extensionContext.getRequiredTestClass(), extensionContext);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        String testId = extensionContext.getRequiredTestClass().getName() + extensionContext.getRequiredTestMethod().getName();
        resetAll(testId, extensionContext.getRequiredTestMethod(), extensionContext);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        String testId = extensionContext.getRequiredTestClass().getName();
        registerStubs(testId, extensionContext.getRequiredTestClass(), extensionContext);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        String testId = extensionContext.getRequiredTestClass().getName() + extensionContext.getRequiredTestMethod().getName();
        registerStubs(testId, extensionContext.getRequiredTestMethod(), extensionContext);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == WiremockServerDefinition.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getWireMockDefinition(extensionContext, extensionContext.getRequiredTestMethod());
    }

    private void registerStubs(String testId, AnnotatedElement annotatedElement, ExtensionContext extensionContext) throws IOException {
        Optional<Wiremock> optionalWiremock = AnnotationSupport.findAnnotation(annotatedElement, Wiremock.class);

        if (optionalWiremock.isPresent()) {
            Wiremock wiremock = optionalWiremock.get();
            WiremockServerDefinition wiremockServerDefinition = getWireMockDefinition(extensionContext, wiremock);
            logger.info("Registering stubs for test " + testId + ", stubs: " + Arrays.stream(wiremock.stubs()).toList());
            wiremockServerDefinition.registerStubs(testId, Arrays.stream(wiremock.stubs()).toList(), wiremock.basePath());
        }
    }

    private void resetAll(String testId, AnnotatedElement annotatedElement, ExtensionContext extensionContext) {
        WiremockServerDefinition wiremockServerDefinition = getWireMockDefinition(extensionContext, annotatedElement);

        if (wiremockServerDefinition != null) {
            logger.info("Resetting all stubs for test " + testId);

            try {
                wiremockServerDefinition.checkRForUnmatchedRequests();
            } finally {
                wiremockServerDefinition.resetAll(testId);
            }
        }
    }

    private WiremockServerDefinition getWireMockDefinition(ExtensionContext extensionContext, AnnotatedElement annotatedElement) {
        Optional<Wiremock> optionalWiremock = AnnotationSupport.findAnnotation(annotatedElement, Wiremock.class);
        return optionalWiremock.map(wiremock -> getWireMockDefinition(extensionContext, wiremock)).orElse(null);
    }

    private WiremockServerDefinition getWireMockDefinition(ExtensionContext extensionContext, Wiremock wiremock) {
        int port = wiremock.port();
        boolean verbose = wiremock.verbose();

        return (WiremockServerDefinition) getStore(extensionContext).getOrComputeIfAbsent(
                "SERVER_" + port,
                (k) -> {
                    logger.info("Creating WireMock server on port " + port);
                    WiremockServerDefinition wiremockServerDefinition = new WiremockServerDefinition(port, verbose);
                    wiremockServerDefinition.start();
                    return wiremockServerDefinition;
                }
        );
    }

    private ExtensionContext.Store getStore(ExtensionContext extensionContext) {
        return extensionContext.getRoot().getStore(WIREMOCK);
    }
}
