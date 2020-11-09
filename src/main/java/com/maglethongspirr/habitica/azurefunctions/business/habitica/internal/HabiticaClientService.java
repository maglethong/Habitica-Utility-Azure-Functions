package com.maglethongspirr.habitica.azurefunctions.business.habitica.internal;

import com.maglethongspirr.habitica.azurefunctions.business.habitica.api.IHabiticaClientService;
import com.maglethongspirr.habitica.azurefunctions.business.habitica.api.Task;
import com.maglethongspirr.habitica.azurefunctions.business.habitica.api.TaskType;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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


  @Value("${com.maglethongspirr.habitica.azurefunctions.business.habitica.internal.baseurl}")
  private String baseUrl;

  @Value("${com.maglethongspirr.habitica.api.id}")
  private String habiticaUserId;
  @Value("${com.maglethongspirr.habitica.api.key}")
  private String habiticaUserApiKey;
  @Value("${com.maglethongspirr.habitica.api.client}")
  private String habiticaApplicationId;

  public HabiticaClientService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplateBuilder = restTemplateBuilder;
  }

  @Override
  public Object someMethod(Object param) {
    return new Object();
  }

  @Override
  public Collection<Task> getTasks(Date filterByDate, TaskType filterByType) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    HttpEntity<?> requestEntity = getRequestEntityWithHeaders();

    UriComponentsBuilder uriBuilder = UriComponentsBuilder
        .fromHttpUrl(baseUrl + "/tasks/user");

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

    if (response.getStatusCode() != HttpStatus.OK){
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
    headers.set("x-api-user", habiticaUserId);
    headers.set("x-api-key", habiticaUserApiKey);
    headers.set("x-client", habiticaApplicationId);
    return new HttpEntity<>(headers);
  }
}
