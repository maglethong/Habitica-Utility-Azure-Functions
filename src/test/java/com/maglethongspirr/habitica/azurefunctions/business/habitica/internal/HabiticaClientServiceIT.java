package com.maglethongspirr.habitica.azurefunctions.business.habitica.internal;

import com.maglethongspirr.habitica.azurefunctions.business.habitica.api.IHabiticaClientService;
import com.maglethongspirr.habitica.azurefunctions.business.habitica.api.Task;
import com.maglethongspirr.habitica.azurefunctions.business.habitica.api.TaskType;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.util.ReflectionTestUtils;

public class HabiticaClientServiceIT {
  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaClientServiceIT.class);

  private IHabiticaClientService service;

  @Before
  public void prepare() throws IOException {
    service = new HabiticaClientService(new RestTemplateBuilder());

    Properties properties = new Properties();
    properties.load(new FileInputStream("./local/local.properties"));

    String habiticaUserId = properties.getProperty("com.maglethongspirr.habitica.api.id");
    String habiticaUserApiKey = properties.getProperty("com.maglethongspirr.habitica.api.key");
    String habiticaApplicationId = properties.getProperty("com.maglethongspirr.habitica.api.client");

    ReflectionTestUtils.setField(service, "habiticaUserId", habiticaUserId);
    ReflectionTestUtils.setField(service, "habiticaUserApiKey", habiticaUserApiKey);
    ReflectionTestUtils.setField(service, "habiticaApplicationId", habiticaApplicationId);

    ReflectionTestUtils.setField(service, "baseUrl", "https://habitica.com/api/v3"); // TODO -> Hardcoded
  }

  @Test
  public void smokeTest() { }

  @Test
  public void test() {
    Collection<Task> result = service.getTasks(null, TaskType.Reward);

    Assert.assertNotNull(result);
    Assert.assertTrue("Result size is 0!", result.size() > 0);
  }
}
