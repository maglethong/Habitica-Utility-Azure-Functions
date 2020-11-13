package com.maglethong.habitica.utility.core.testutils;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for Tasting using MockClientAndServer.
 *
 * @param <T> The actual test class (Used so each Test Class has its own Mocked Server)
 */
public abstract class AbstractBaseClientTest<T extends AbstractBaseClientTest> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBaseClientTest.class);
  private static final String MOCK_SERVER_HOST = "127.0.0.1";
  private static final String BASE_URL = "http://" + MOCK_SERVER_HOST + ":{mockServerPort}";
  private static int mockServerPort;
  private static ClientAndServer mockServer;
  private Logger baseLogger = LoggerFactory.getLogger(this.getClass());

  protected static String getBaseUrl() {
    return BASE_URL.replace("{mockServerPort}", Integer.toString(mockServerPort));
  }

  protected static int getMockServerPort() {
    return mockServerPort;
  }

  protected static MockServerClient createClientMock() {
    return new MockServerClient(MOCK_SERVER_HOST, mockServerPort);
  }

  @BeforeAll
  protected static void beforeAll() throws IOException {
    startClientAndServer(0);
  }

  private static void startClientAndServer(int attemptsLeft) throws IOException {
    try {
      // Randomize Port
      ServerSocket socket = new ServerSocket(0);
      mockServerPort = socket.getLocalPort();
      socket.close();

      // Start server
      mockServer = ClientAndServer.startClientAndServer(mockServerPort);
      LOGGER.info("Mock Server initialized on {}", getBaseUrl());

      // Attempt again if port taken
    } catch (RuntimeException e) {
      if (!e.getCause().getClass().equals(BindException.class)) {
        throw e;
      }
      LOGGER.warn("Failed to start at port {}", mockServerPort);
      if (attemptsLeft > 0) {
        LOGGER.error("Mock Server initialisation Attempts exhausted");
        throw e;
      }
      LOGGER.info("Retrying Mock Server initialisation");
      startClientAndServer(attemptsLeft - 1);
    }
  }

  @AfterAll
  protected static void afterAll() {
    LOGGER.debug("Stopping MockServer");
    mockServer.stop();
  }

  protected Logger getLogger() {
    return baseLogger;
  }

  @AfterEach
  protected void afterEach() {
    LOGGER.debug("Resetting MockServer");
    mockServer.reset();
  }
}
