package com.maglethongspirr.habitica.azurefunctions.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maglethongspirr.habitica.azurefunctions.Application;
import com.maglethongspirr.habitica.azurefunctions.business.habitica.api.IHabiticaClientService;
import com.maglethongspirr.habitica.azurefunctions.business.habitica.api.Task;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = Application.class,
    properties = {
        "com.maglethongspirr.habitica.test.prop=asd"
    }
)
@ActiveProfiles("test")
public class HabiticaWebHookControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaWebHookControllerTest.class);
  private TestRestTemplate restTemplate = new TestRestTemplate();

  @MockBean
  private IHabiticaClientService habiticaClientService;

  @LocalServerPort
  private int port;

  private String getEndpointUrl() {
    return "http://localhost:{port}/api/v1/habitica/webhook"
        .replace("{port}", Integer.toString(port));
  }

  @Test
  public void smokeTest() {
  }

  @Test
  public void someTest() throws IOException {
    // Prepare
    InputStream stream = this.getClass().getResourceAsStream("tasks_user_01.json");
    Assert.assertNotNull("[Initialisation error] Could not find test resource.", stream);

    List<Task> tasks = new ObjectMapper().readValue(stream, new TypeReference<List<Task>>() {});
    Assert.assertNotNull("[Initialisation error] Could not read test resource.", tasks);
    Assert.assertTrue("[Initialisation error] Could not read test resource.", tasks.size() > 0);

    Mockito
        .when(habiticaClientService.getTasks(null, null))
        .thenReturn(tasks);

    // Run
    String url = getEndpointUrl() + "/42";
    Object response = restTemplate.postForObject(url, "Body", Object.class);

    // Assert
    Assert.assertNotNull("Response was null", response);

    Mockito
        .verify(habiticaClientService)
        .someMethod(Mockito.any(Object.class));
  }
}
