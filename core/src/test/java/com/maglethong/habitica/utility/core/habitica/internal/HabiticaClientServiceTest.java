package com.maglethong.habitica.utility.core.habitica.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maglethong.habitica.utility.core.config.AppProperties;
import com.maglethong.habitica.utility.core.habitica.api.Attribute;
import com.maglethong.habitica.utility.core.habitica.api.Task;
import com.maglethong.habitica.utility.core.habitica.api.TaskType;
import com.maglethong.habitica.utility.core.testutils.BaseClientTest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;

@ExtendWith(MockitoExtension.class)
class HabiticaClientServiceTest extends BaseClientTest<HabiticaClientServiceTest> {

  @Mock
  private AppProperties appProperties;

  @InjectMocks
  private HabiticaClientService service;

  @BeforeEach
  void prepare() {
    Mockito
        .lenient()
        .when(appProperties.getHabiticaBaseUrl())
        .thenReturn(getBaseUrl());
  }

  @Test
  void smokeTest() {}

  @Test
  void testGetTask() throws IOException {
    /////////////
    // Prepare //
    /////////////
    InputStream stream = this.getClass().getResourceAsStream("tasks_user_01.json");
    Assert.assertNotNull("[Initialisation error] Could not find test resource.", stream);
    String tasks_s = IOUtils.toString(stream, StandardCharsets.UTF_8).replaceAll("[\n\\s]", "");

    HabiticaRequestResult<List<Task>> tasks = new ObjectMapper()
        .readValue(tasks_s, new TypeReference<HabiticaRequestResult<List<Task>>>() {});
    Assert.assertNotNull("[Initialisation error] Could not read test resource.", tasks);
    Assert.assertNotNull("[Initialisation error] Could not read test resource.", tasks.data);
    Assert.assertTrue("[Initialisation error] Could not read test resource.", tasks.data.size() > 0);

    createClientMock()
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

    /////////////
    //   Run   //
    /////////////
    Collection<Task> result = service.getTasks(null, TaskType.Reward);

    /////////////
    //  Assert //
    /////////////
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

  @Test
  void testUpdateTask() throws IOException {
    /////////////
    // Prepare //
    /////////////
    InputStream stream_orig = this.getClass().getResourceAsStream("tasks_user_01.json");
    Assert.assertNotNull("[Initialisation error] Could not find test resource 1.", stream_orig);
    String tasks_orig_s = IOUtils.toString(stream_orig, StandardCharsets.UTF_8).replaceAll("[\n\\s]", "");

    HabiticaRequestResult<List<Task>> tmp = new ObjectMapper()
        .readValue(tasks_orig_s, new TypeReference<HabiticaRequestResult<List<Task>>>() {});
    Assert.assertNotNull("[Initialisation error] Could not read test resource 1.", tmp);
    Assert.assertNotNull("[Initialisation error] Could not read test resource 1.", tmp.data);
    Assert.assertTrue("[Initialisation error] Could not read test resource 1.", tmp.data.size() > 0);
    Task task_orig = tmp.data.get(0);


    Task task_updated = ((Task) task_orig.clone())
        .setTaskValue((short) 50)
        .setAttribute(Attribute.Constitution)
        .setNotes(task_orig.getNotes() + " updated")
        .setTagIds(Arrays.asList(task_orig.getTagIds().get(0), "tag_id_updated"))
        .setPriority((short) 2)
        .setText(task_orig.getText() + " Updated");

    Task taskUpdateValues = new Task()
        .setTaskValue(task_updated.getTaskValue())
        .setAttribute(task_updated.getAttribute())
        .setNotes(task_orig.getNotes())
        .setTagIds(task_orig.getTagIds())
        .setPriority(task_orig.getPriority())
        .setText(task_orig.getText());

    String taskUpdateValues_s = new ObjectMapper().writeValueAsString(taskUpdateValues);

    HabiticaRequestResult<Task> response = new HabiticaRequestResult<>(task_updated);
    String response_s = new ObjectMapper().writeValueAsString(response);

    createClientMock()
        .when(HttpRequest
                .request()
                .withMethod("PUT")
                .withPath("/tasks/" + task_orig.getId())
                .withBody(taskUpdateValues_s, StandardCharsets.UTF_8),
            Times.exactly(1))
        .respond(HttpResponse
            .response()
            .withStatusCode(200)
            .withHeader("Content-Type", "application/json")
            .withBody(response_s));

    /////////////
    //   Run   //
    /////////////
    Task result = service.updateTask(task_orig.getId(), taskUpdateValues);

    /////////////
    //  Assert //
    /////////////
    Assert.assertNotNull("Result was null", result);
    Assert.assertEquals(task_updated.getAttribute(), result.getAttribute());
    Assert.assertEquals(task_updated.getId(), result.getId());
    Assert.assertEquals(task_updated.getNotes(), result.getNotes());
    Assert.assertEquals(task_updated.getPriority(), result.getPriority());
    Assert.assertEquals(task_updated.getTagIds(), result.getTagIds());
    Assert.assertEquals(task_updated.getTaskType(), result.getTaskType());
    Assert.assertEquals(task_updated.getTaskValue(), result.getTaskValue());
    Assert.assertEquals(task_updated.getText(), result.getText());
    Assert.assertEquals(task_updated.getCreatedAt(), result.getCreatedAt());
  }
}
