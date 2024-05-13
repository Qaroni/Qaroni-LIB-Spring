package com.qaroni.libs.tests.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.common.Metadata.metadata;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.platform.commons.util.StringUtils.isBlank;

public class WiremockServerDefinition implements ExtensionContext.Store.CloseableResource {
    private final Logger logger = Logger.getLogger(WiremockServerDefinition.class.getName());

    private final int port;
    private final WireMockServer wireMockServer;

    public WiremockServerDefinition(int port, boolean verbose) {
        this.port = port;
        this.wireMockServer = new WireMockServer(options().port(port).notifier(new Slf4jNotifier(verbose)));
    }

    public void start() {
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
            WireMock.configureFor(port);
            logger.info("Wiremock server started on port " + port);
        }
    }

    public void stop() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
            logger.info("Wiremock server stopped on port " + port);
        }
    }

    public void checkRForUnmatchedRequests() {
        wireMockServer.checkForUnmatchedRequests();
    }

    public void resetAll() {
        wireMockServer.resetAll();
    }

    public void resetAll(String groupId) {
        wireMockServer.removeStubMappingsByMetadata(matchingJsonPath("[?(@.groupId == '$groupId')]"));
        wireMockServer.resetRequests();
    }

    public void registerStubs(String groupId, List<String> stubsList, String basePath) throws IOException {
        for (String stubs: stubsList) {
            logger.info("Processing stubs: " + stubs);

            if (isBlank(stubs)) {
                continue;
            }

            try (Stream<Path> walk = Files.walk(new File(basePath + stubs).toPath())) {
                walk.forEach(stubFile -> {
                    if (Files.isRegularFile(stubFile)) {
                        logger.info("Processing stub file: " + stubFile);

                        try {
                            StubMapping stubMapping = StubMapping.buildFrom(Files.readString(stubFile));
                            stubMapping.setMetadata(metadata().attr("groupId", groupId).build());
                            wireMockServer.addStubMapping(stubMapping);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void close() {
        stop();
    }
}
