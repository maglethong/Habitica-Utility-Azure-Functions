package com.maglethong.habitica.utility.core.habitica.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookActivity {
  private WebHookType type;
  private WebHookActivityType activityType;
  private Task task;

  //private String user;
  //private Group group;
  //private Quest quest;


  public WebHookType getType() {
    return type;
  }

  public WebhookActivity setType(WebHookType type) {
    this.type = type;
    return this;
  }

  public WebHookActivityType getActivityType() {
    return activityType;
  }

  public WebhookActivity setActivityType(
      WebHookActivityType activityType) {
    this.activityType = activityType;
    return this;
  }

  public Task getTask() {
    return task;
  }

  public WebhookActivity setTask(Task task) {
    this.task = task;
    return this;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("WebhookActivity{");
    sb.append("type=").append(type);
    sb.append(", activityType=").append(activityType);
    sb.append(", task=").append(task);
    sb.append('}');
    return sb.toString();
  }
}
