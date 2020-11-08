package com.maglethongspirr.habitica.azurefunctions.business.habitica.internal;

import com.maglethongspirr.habitica.azurefunctions.api.HabiticaWebHookControllerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class HabiticaClientServiceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaWebHookControllerTest.class);

  @InjectMocks
  private HabiticaClientService service;

  @Test
  public void smokeTest() { }

  @Test
  public void someTest() {
    // Prepare
    //...

    // Run
    Object result = service.someMethod(new Object());

    // Assert
    Assert.assertNotNull("Result was null", result);
  }
}
