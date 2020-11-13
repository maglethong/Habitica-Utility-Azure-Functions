package com.maglethong.habitica.utility.core.habitica.internal;

import com.maglethong.habitica.utility.core.config.AppProperties;
import com.maglethong.habitica.utility.core.habitica.api.Attribute;
import com.maglethong.habitica.utility.core.habitica.api.Task;
import com.maglethong.habitica.utility.core.habitica.api.TaskType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class HabiticaClientServiceIT {
  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaClientServiceIT.class);

  @Mock
  private AppProperties appProperties;

  @InjectMocks
  private HabiticaClientService service;

  @BeforeEach
  void prepare() throws IOException {
    InputStream propStream = this.getClass().getResourceAsStream("/local.properties");
    Assertions.assertNotNull(propStream, "local.properties resource not found");

    Properties properties = new Properties();
    properties.load(propStream);

    Mockito
        .when(appProperties.getHabiticaBaseUrl())
        .thenReturn(properties.getProperty("com.maglethongspirr.habitica.core.habitica.api.url"));
    Mockito
        .when(appProperties.getHabiticaUserId())
        .thenReturn(properties.getProperty("com.maglethongspirr.habitica.core.habitica.api.id"));
    Mockito
        .when(appProperties.getHabiticaUserApiKey())
        .thenReturn(properties.getProperty("com.maglethongspirr.habitica.core.habitica.api.key"));
    Mockito
        .when(appProperties.getHabiticaApplicationId())
        .thenReturn(properties.getProperty("com.maglethongspirr.habitica.core.habitica.api.client"));
  }

  @Test
  void smokeTest() {
  }

  @Test
  void testGetTasks() {
    Collection<Task> result = service.getTasks(null, TaskType.Reward);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.size() > 0, "Result size is 0!");
  }

  @Test
  void testUpdateTask() {
    /////////////
    // Prepare //
    /////////////
    Collection<Task> getResult = service.getTasks(null, TaskType.Reward);

    Assertions.assertNotNull(getResult);
    Assertions.assertTrue(getResult.size() > 0, "Result size is 0!");

    Task target = getResult.iterator().next();

    /////////////
    //   Run   //
    /////////////
    Task updateValues = new Task().setAttribute(Attribute.Constitution);
    Task updated = service.updateTask(target.getId(), updateValues);

    /////////////
    //  Assert //
    /////////////

    // Unchanged
    Assertions.assertEquals(target.getId(), updated.getId());
    Assertions.assertEquals(target.getNotes(), updated.getNotes());
    Assertions.assertEquals(target.getPriority(), updated.getPriority());
    Assertions.assertEquals(target.getTagIds(), updated.getTagIds());
    Assertions.assertEquals(target.getTaskType(), updated.getTaskType());
    Assertions.assertEquals(target.getTaskValue(), updated.getTaskValue());
    Assertions.assertEquals(target.getText(), updated.getText());
    Assertions.assertEquals(target.getCreatedAt(), updated.getCreatedAt());

    // Updated
    Assertions.assertEquals(updateValues.getAttribute(), updated.getAttribute());

    // Rollback
    updateValues = new Task().setAttribute(target.getAttribute());
    service.updateTask(target.getId(), updateValues);
  }
}
