package com.maglethong.habitica.utility.core.habitica.internal;

import com.maglethong.habitica.utility.core.config.AppProperties;
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
import org.springframework.boot.web.client.RestTemplateBuilder;

@RunWith(MockitoJUnitRunner.class)
public class HabiticaClientServiceIT {
  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaClientServiceIT.class);

  @Mock
  private AppProperties appProperties;
  @Mock
  private RestTemplateBuilder restTemplateBuilder;

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

    Mockito
        .when(restTemplateBuilder.build())
        .thenReturn(new RestTemplateBuilder().build());
  }

  @Test
  public void smokeTest() {
    LOGGER.debug("debug");
    LOGGER.info("info");
    LOGGER.error("error"); // TODO -> verify this is working
  }

  @Test
  public void test() {
    Collection<Task> result = service.getTasks(null, TaskType.Reward);

    Assert.assertNotNull(result);
    Assert.assertTrue("Result size is 0!", result.size() > 0);
  }
}
