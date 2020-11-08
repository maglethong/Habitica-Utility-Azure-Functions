package com.maglethongspirr.habitica.azurefunctions.business.habitica.internal;

import com.maglethongspirr.habitica.azurefunctions.business.habitica.api.IHabiticaClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HabiticaClientService implements IHabiticaClientService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaClientService.class);

  @Override
  public Object someMethod(Object param) {
    return new Object();
  }
}
