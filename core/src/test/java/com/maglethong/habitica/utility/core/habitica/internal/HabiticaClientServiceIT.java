package com.maglethong.habitica.utility.core.habitica.internal;

import com.maglethong.habitica.utility.core.config.AppProperties;
import com.maglethong.habitica.utility.core.habitica.api.Attribute;
import com.maglethong.habitica.utility.core.habitica.api.Task;
import com.maglethong.habitica.utility.core.habitica.api.TaskType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class HabiticaClientServiceIT {
  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaClientServiceIT.class);

  @Mock
  private AppProperties appProperties;

  @InjectMocks
  private HabiticaClientService service;

  @Before
  public void prepare() throws IOException {
    InputStream propStream = this.getClass().getResourceAsStream("/local.properties");
    Assert.assertNotNull("local.properties resource not found", propStream);

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
  public void smokeTest() {
  }

  @Test
  public void testGetTasks() {
    Collection<Task> result = service.getTasks(null, TaskType.Reward);

    Assert.assertNotNull(result);
    Assert.assertTrue("Result size is 0!", result.size() > 0);
  }

  @Test
  public void testUpdateTask() {
    /////////////
    // Prepare //
    /////////////
    Collection<Task> getResult = service.getTasks(null, TaskType.Reward);

    Assert.assertNotNull(getResult);
    Assert.assertTrue("Result size is 0!", getResult.size() > 0);

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
    Assert.assertEquals(target.getId(), updated.getId());
    Assert.assertEquals(target.getNotes(), updated.getNotes());
    Assert.assertEquals(target.getPriority(), updated.getPriority());
    Assert.assertEquals(target.getTagIds(), updated.getTagIds());
    Assert.assertEquals(target.getTaskType(), updated.getTaskType());
    Assert.assertEquals(target.getTaskValue(), updated.getTaskValue());
    Assert.assertEquals(target.getText(), updated.getText());
    Assert.assertEquals(target.getCreatedAt(), updated.getCreatedAt());

    // Updated
    Assert.assertEquals(updateValues.getAttribute(), updated.getAttribute());

    // Rollback
    updateValues = new Task().setAttribute(target.getAttribute());
    service.updateTask(target.getId(), updateValues);
  }
}
