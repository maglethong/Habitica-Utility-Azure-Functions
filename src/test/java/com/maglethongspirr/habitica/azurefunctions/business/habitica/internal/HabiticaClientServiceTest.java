package com.maglethongspirr.habitica.azurefunctions.business.habitica.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maglethongspirr.habitica.azurefunctions.api.HabiticaWebHookControllerTest;
import com.maglethongspirr.habitica.azurefunctions.business.habitica.api.Task;
import com.maglethongspirr.habitica.azurefunctions.business.habitica.api.TaskType;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class HabiticaClientServiceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaWebHookControllerTest.class);

  private static final String MOCK_SERVER_HOST = "127.0.0.1";
  private static final String BASE_URL = "http://" + MOCK_SERVER_HOST + ":{mockServerPort}";
  private static int mockServerPort;
  private static ClientAndServer mockServer;

  @Mock
  private RestTemplateBuilder restTemplateBuilder;

  @InjectMocks
  private HabiticaClientService service;

  @BeforeClass
  public static void setup() throws IOException {
    startClientAndServer(0);
  }

  private static void startClientAndServer(int attempt) throws IOException {
    try {
      ServerSocket socket = new ServerSocket(0);
      mockServerPort = socket.getLocalPort();
      socket.close();
      mockServer = ClientAndServer.startClientAndServer(mockServerPort);
      LOGGER.info("Retrying Mock Server initialized on port {}", mockServerPort); // TODO -> Fix logging in this case
    } catch (RuntimeException e) {
      if (!e.getCause().getClass().equals(BindException.class)) {
        throw e;
      }
      LOGGER.warn("Failed to start at port {}", mockServerPort);
      if (attempt > 5) {
        LOGGER.error("Mock Server initialisation Attempts exhausted");
        throw e;
      }
      LOGGER.info("Retrying Mock Server initialisation");
      startClientAndServer(attempt + 1);
    }
  }

  @AfterClass
  public static void stopServer() {
    mockServer.stop();
  }

  @Before
  public void prepare() {
    String baseUrl = BASE_URL.replace("{mockServerPort}", Integer.toString(mockServerPort));
    ReflectionTestUtils.setField(service, "baseUrl", baseUrl);

    Mockito
        .when(restTemplateBuilder.build())
        .thenReturn(new RestTemplateBuilder().build());
  }

  @Test
  public void smokeTest() { }

  @Test
  public void testGetTask() throws IOException {
    // Prepare
    InputStream stream = this.getClass().getResourceAsStream("tasks_user_01.json");
    Assert.assertNotNull("[Initialisation error] Could not find test resource.", stream);
    String tasks_s = IOUtils.toString(stream, StandardCharsets.UTF_8);

    HabiticaRequestResult<List<Task>> tasks = new ObjectMapper()
        .readValue(tasks_s, new TypeReference<HabiticaRequestResult<List<Task>>>() {});
    Assert.assertNotNull("[Initialisation error] Could not read test resource.", tasks);
    Assert.assertNotNull("[Initialisation error] Could not read test resource.", tasks.data);
    Assert.assertTrue("[Initialisation error] Could not read test resource.", tasks.data.size() > 0);

    new MockServerClient(MOCK_SERVER_HOST, mockServerPort)
        .when(HttpRequest
                .request()
                .withMethod("GET")
                .withPath("/tasks/user")
                .withQueryStringParameter(Parameter.param("type")),
            Times.exactly(1))
        .respond(HttpResponse
            .response()
            .withStatusCode(200)
            .withHeader("Content-Type", "application/json")
            .withBody(tasks_s));

    // Run
    Collection<Task> result = service.getTasks(null, TaskType.Reward);

    // Assert
    Assert.assertNotNull("Result was null", result);
    Assert.assertEquals("Unexpected amount of tasks", tasks.data.size(), result.size());
    for (Task task : tasks.data) {
      Assert.assertTrue("Expected task was not found " + task.getId(), result.stream().anyMatch(t -> t.equals(task)));
    }
    for (Task task : result) {
      Assert.assertNotNull(task.getAttribute());
      Assert.assertNotNull(task.getCreatedAt());
      Assert.assertNotNull(task.getId());
      Assert.assertNotNull(task.getNotes());
      Assert.assertNotNull(task.getPriority());
      Assert.assertNotNull(task.getTagIds());
      Assert.assertNotNull(task.getTaskType());
      Assert.assertNotNull(task.getTaskValue());
      Assert.assertNotNull(task.getText());
      Assert.assertNotNull(task.getUpdatedAt());
    }
  }
}
