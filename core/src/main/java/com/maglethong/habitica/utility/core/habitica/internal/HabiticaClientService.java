package com.maglethong.habitica.utility.core.habitica.internal;

import com.google.inject.Inject;
import com.maglethong.habitica.utility.core.config.AppProperties;
import com.maglethong.habitica.utility.core.habitica.api.IHabiticaClientService;
import com.maglethong.habitica.utility.core.habitica.api.Task;
import com.maglethong.habitica.utility.core.habitica.api.TaskType;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class HabiticaClientService implements IHabiticaClientService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaClientService.class);

  private final RestTemplateBuilder restTemplateBuilder;
  private final AppProperties properties;

  @Inject
  @Autowired
  public HabiticaClientService(AppProperties properties, RestTemplateBuilder restTemplateBuilder) {
    this.restTemplateBuilder = restTemplateBuilder;
    this.properties = properties;
  }

  @Override
  public Collection<Task> getTasks(Date filterByDate, TaskType filterByType) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    HttpEntity<?> requestEntity = getRequestEntityWithHeaders();

    UriComponentsBuilder uriBuilder = UriComponentsBuilder
        .fromHttpUrl(properties.getHabiticaBaseUrl() + "/tasks/user");

    if (filterByDate != null) {
      uriBuilder = uriBuilder.queryParam("", filterByDate); // TODO -> format correctly
    }

    if (filterByType != null) {
      uriBuilder = uriBuilder.queryParam("type", filterByType.getQueryValue());
    }

    LOGGER.info("Sending request GET {}", uriBuilder.toUriString());
    ResponseEntity<HabiticaRequestResult<List<Task>>> response = restTemplate.exchange(
        uriBuilder.toUriString(),
        HttpMethod.GET,
        requestEntity,
        new ParameterizedTypeReference<HabiticaRequestResult<List<Task>>>() {});

    if (response.getStatusCode() != HttpStatus.OK) {
      // TODO -> treat response codes;
    }

    if (response.getBody() == null || response.getBody().data == null) {
      // TODO -> tread error
    }

    LOGGER.debug("Got data of size: ", response.getBody().data.size());
    return response.getBody().data;
  }

  @Override
  public boolean updateTask(String taskId, Task task) {
    return false;
  }

  private HttpEntity getRequestEntityWithHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.set("x-api-user", properties.getHabiticaUserId());
    headers.set("x-api-key", properties.getHabiticaUserApiKey());
    headers.set("x-client", properties.getHabiticaApplicationId());
    return new HttpEntity<>(headers);
  }
}
