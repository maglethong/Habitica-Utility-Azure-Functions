package com.maglethong.habitica.utility.core.habitica.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.maglethong.habitica.utility.core.config.AppProperties;
import com.maglethong.habitica.utility.core.habitica.api.IHabiticaClientService;
import com.maglethong.habitica.utility.core.habitica.api.Task;
import com.maglethong.habitica.utility.core.habitica.api.TaskType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HabiticaClientService implements IHabiticaClientService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaClientService.class);
  private static final String HTTP_GET = "GET";
  private static final String HTTP_PUT = "PUT";
  private final ObjectMapper objectMapper;
  private final AppProperties properties;

  @Inject
  @Autowired
  public HabiticaClientService(AppProperties properties) {
    this.properties = properties;
    objectMapper = new ObjectMapper();
  }

  @Override
  public Collection<Task> getTasks(Date filterByDate, TaskType filterByType) {
    LOGGER.debug("HabiticaClientService#getTasks({}, {}) called", filterByDate, filterByType);

    try {
      URIBuilder builder = new URIBuilder(properties.getHabiticaBaseUrl() + "/tasks/user");
      if (filterByDate != null) {
        builder
            .setParameter("_____", filterByDate.toString()); // TODO -> format correctly
      }
      if (filterByType != null) {
        builder
            .setParameter("type", filterByType.getQueryValue());
      }
      final URI uri = builder.build();
      LOGGER.info("Calling {} '{}'", HTTP_GET, uri);

      final HttpClient client = getHttpClientWithHeaders();
      final HttpUriRequest request = RequestBuilder
          .create(HTTP_GET)
          .setUri(uri)
          .build();
      final HttpResponse response = client.execute(request);
      final String responseAsString = EntityUtils.toString(response.getEntity());
      final int statusCode = response.getStatusLine().getStatusCode();
      LOGGER.trace("Got response: {}", responseAsString);

      if (statusCode != HttpStatus.SC_OK) {
        throw new RuntimeException("Status code: " + statusCode); // TODO
      }

      final HabiticaRequestResult<List<Task>> responseObject = objectMapper.readValue(responseAsString,
          new TypeReference<HabiticaRequestResult<List<Task>>>() {});

      if (responseObject == null || responseObject.data == null) {
        throw new RuntimeException("Got no data in response"); // TODO
      }

      return responseObject.data;

    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    } catch (ClientProtocolException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Task updateTask(String taskId, Task task) {
    LOGGER.debug("HabiticaClientService#updateTask({}, {}) called", taskId, task);

    try { // TODO -> Generalize
      String body = objectMapper.writeValueAsString(task);
      URIBuilder builder = new URIBuilder(properties.getHabiticaBaseUrl() + "/tasks/" + taskId);
      final URI uri = builder.build();
      LOGGER.info("Calling {} '{}' with body size {}", HTTP_PUT, uri, body.length());
      LOGGER.debug(" - Body content {}", body);

      final HttpClient client = getHttpClientWithHeaders();
      final HttpUriRequest request = RequestBuilder
          .create(HTTP_PUT)
          .setUri(uri)
          .setEntity(new StringEntity(body, ContentType.APPLICATION_JSON))
          .build();
      final HttpResponse response = client.execute(request);
      final String responseAsString = EntityUtils.toString(response.getEntity());
      final int statusCode = response.getStatusLine().getStatusCode();
      LOGGER.trace("Got response: {}", responseAsString);

      if (statusCode != HttpStatus.SC_OK) {
        throw new RuntimeException("Status code: " + statusCode); // TODO
      }

      final HabiticaRequestResult<Task> responseObject = objectMapper.readValue(responseAsString,
          new TypeReference<HabiticaRequestResult<Task>>() {});

      if (responseObject == null || responseObject.data == null) {
        throw new RuntimeException("Got no data in response"); // TODO
      }

      return responseObject.data;

    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    } catch (ClientProtocolException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private HttpClient getHttpClientWithHeaders() {
    final List<Header> headers = Arrays.asList(
        new BasicHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType()),
        new BasicHeader("x-api-user", properties.getHabiticaUserId()),
        new BasicHeader("x-api-key", properties.getHabiticaUserApiKey()),
        new BasicHeader("x-client", properties.getHabiticaApplicationId())
    );
    return HttpClients
        .custom()
        .setDefaultHeaders(headers)
        .build();
  }
}
