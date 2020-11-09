package com.maglethong.habitica.utility.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

  @Value("${com.maglethongspirr.habitica.core.habitica.api.url}")
  private String habiticaBaseUrl;

  @Value("${com.maglethongspirr.habitica.core.habitica.api.id:}")
  private String habiticaUserId;
  @Value("${com.maglethongspirr.habitica.core.habitica.api.key:}")
  private String habiticaUserApiKey;
  @Value("${com.maglethongspirr.habitica.core.habitica.api.client:}")
  private String habiticaApplicationId;

  public String getHabiticaBaseUrl() {
    return habiticaBaseUrl;
  }

  public String getHabiticaUserId() {
    return habiticaUserId;
  }

  public String getHabiticaUserApiKey() {
    return habiticaUserApiKey;
  }

  public String getHabiticaApplicationId() {
    return habiticaApplicationId;
  }

  // TODO -> Loader for non-Spring
}
