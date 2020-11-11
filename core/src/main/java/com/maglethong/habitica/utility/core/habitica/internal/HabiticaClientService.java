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
      LOGGER.info("Calling GET '{}'", uri);

      final HttpClient client = getHttpClientWithHeaders();
      final HttpUriRequest request = RequestBuilder
          .get()
          .setUri(uri)
          .build();
      final HttpResponse response = client.execute(request);
      final String responseAsString = EntityUtils.toString(response.getEntity());
      final int statusCode = response.getStatusLine().getStatusCode();

      if (statusCode != HttpStatus.SC_OK) {
        throw new RuntimeException("Status code: " + statusCode); // TODO
      }

      final HabiticaRequestResult<List<Task>> responseObject = objectMapper.readValue(responseAsString,
          new TypeReference<HabiticaRequestResult<List<Task>>>() {});

      if (responseObject == null || responseObject.data == null) {
        throw new RuntimeException("Got no data in response"); // TODO
      }

      LOGGER.debug("Got data of size: {}", responseObject.data.size());
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
  public boolean updateTask(String taskId, Task task) {
    return false;
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
