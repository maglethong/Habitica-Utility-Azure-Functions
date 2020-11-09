import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maglethong.habitica.utility.core.habitica.api.IHabiticaClientService;
import com.maglethong.habitica.utility.core.habitica.api.Task;
import com.maglethong.habitica.utility.core.habitica.api.TaskType;
import com.maglethong.habitica.utility.spring.Application;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
    List<Task> tasks = Collections.singletonList(new Task().setId("Task_id_42"));

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
        .getTasks(Mockito.any(Date.class), Mockito.any(TaskType.class));
  }
}
