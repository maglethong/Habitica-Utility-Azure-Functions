package com.maglethong.habitica.utility.core.habitica.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maglethong.habitica.utility.core.config.AppProperties;
import com.maglethong.habitica.utility.core.habitica.api.Attribute;
import com.maglethong.habitica.utility.core.habitica.api.Task;
import com.maglethong.habitica.utility.core.habitica.api.TaskType;
import com.maglethong.habitica.utility.core.testutils.AbstractBaseClientTest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
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
class HabiticaClientServiceTest extends AbstractBaseClientTest<HabiticaClientServiceTest> {

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
    Assertions.assertNotNull(stream, "[Initialisation error] Could not find test resource.");
    String tasksAsString = IOUtils.toString(stream, StandardCharsets.UTF_8).replaceAll("[\n\\s]", "");

    HabiticaRequestResult<List<Task>> tasks = new ObjectMapper()
        .readValue(tasksAsString, new TypeReference<HabiticaRequestResult<List<Task>>>() {});
    Assertions.assertNotNull(tasks, "[Initialisation error] Could not read test resource.");
    Assertions.assertNotNull(tasks.data, "[Initialisation error] Could not read test resource.");
    Assertions.assertTrue(tasks.data.size() > 0, "[Initialisation error] Could not read test resource.");

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
            .withBody(tasksAsString));

    /////////////
    //   Run   //
    /////////////
    Collection<Task> result = service.getTasks(null, TaskType.Reward);

    /////////////
    //  Assert //
    /////////////
    Assertions.assertNotNull(result, "Result was null");
    Assertions.assertEquals(tasks.data.size(), result.size(), "Unexpected amount of tasks");
    for (Task task : tasks.data) {
      Assertions
          .assertTrue(result.stream().anyMatch(t -> t.equals(task)), "Expected task was not found " + task.getId());
    }
    for (Task task : result) {
      Assertions.assertNotNull(task.getAttribute());
      Assertions.assertNotNull(task.getCreatedAt());
      Assertions.assertNotNull(task.getId());
      Assertions.assertNotNull(task.getNotes());
      Assertions.assertNotNull(task.getPriority());
      Assertions.assertNotNull(task.getTagIds());
      Assertions.assertNotNull(task.getTaskType());
      Assertions.assertNotNull(task.getTaskValue());
      Assertions.assertNotNull(task.getText());
      Assertions.assertNotNull(task.getUpdatedAt());
    }
  }

  @Test
  void testUpdateTask() throws IOException {
    /////////////
    // Prepare //
    /////////////
    InputStream streamOrig = this.getClass().getResourceAsStream("tasks_user_01.json");
    Assertions.assertNotNull(streamOrig, "[Initialisation error] Could not find test resource 1.");
    String tasksOrigAsString = IOUtils.toString(streamOrig, StandardCharsets.UTF_8).replaceAll("[\n\\s]", "");

    HabiticaRequestResult<List<Task>> tmp = new ObjectMapper()
        .readValue(tasksOrigAsString, new TypeReference<HabiticaRequestResult<List<Task>>>() {});
    Assertions.assertNotNull(tmp, "[Initialisation error] Could not read test resource 1.");
    Assertions.assertNotNull(tmp.data, "[Initialisation error] Could not read test resource 1.");
    Assertions.assertTrue(tmp.data.size() > 0, "[Initialisation error] Could not read test resource 1.");
    Task taskOrig = tmp.data.get(0);


    Task taskUpdated = ((Task) taskOrig.clone())
        .setTaskValue((short) 50)
        .setAttribute(Attribute.Constitution)
        .setNotes(taskOrig.getNotes() + " updated")
        .setTagIds(Arrays.asList(taskOrig.getTagIds().get(0), "tag_id_updated"))
        .setPriority((short) 2)
        .setText(taskOrig.getText() + " Updated");

    Task taskUpdateValues = new Task()
        .setTaskValue(taskUpdated.getTaskValue())
        .setAttribute(taskUpdated.getAttribute())
        .setNotes(taskUpdated.getNotes())
        .setTagIds(taskUpdated.getTagIds())
        .setPriority(taskUpdated.getPriority())
        .setText(taskUpdated.getText());

    String taskUpdateValuesAsString = new ObjectMapper().writeValueAsString(taskUpdateValues);

    HabiticaRequestResult<Task> response = new HabiticaRequestResult<>(taskUpdated);
    String responseAsString = new ObjectMapper().writeValueAsString(response);

    createClientMock()
        .when(HttpRequest
                .request()
                .withMethod("PUT")
                .withPath("/tasks/" + taskOrig.getId())
                .withBody(taskUpdateValuesAsString, StandardCharsets.UTF_8),
            Times.exactly(1))
        .respond(HttpResponse
            .response()
            .withStatusCode(200)
            .withHeader("Content-Type", "application/json")
            .withBody(responseAsString));

    /////////////
    //   Run   //
    /////////////
    Task result = service.updateTask(taskOrig.getId(), taskUpdateValues);

    /////////////
    //  Assert //
    /////////////
    Assertions.assertNotNull(result, "Result was null");
    Assertions.assertEquals(taskUpdated.getAttribute(), result.getAttribute());
    Assertions.assertEquals(taskUpdated.getId(), result.getId());
    Assertions.assertEquals(taskUpdated.getNotes(), result.getNotes());
    Assertions.assertEquals(taskUpdated.getPriority(), result.getPriority());
    Assertions.assertEquals(taskUpdated.getTagIds(), result.getTagIds());
    Assertions.assertEquals(taskUpdated.getTaskType(), result.getTaskType());
    Assertions.assertEquals(taskUpdated.getTaskValue(), result.getTaskValue());
    Assertions.assertEquals(taskUpdated.getText(), result.getText());
    Assertions.assertEquals(taskUpdated.getCreatedAt(), result.getCreatedAt());
  }
}
