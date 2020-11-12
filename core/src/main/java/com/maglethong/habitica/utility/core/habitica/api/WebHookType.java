package com.maglethong.habitica.utility.core.habitica.api;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WebHookType {
  TaskActivity("taskActivity", "taskActivity"),
  GroupChatReceived("groupChatReceived", "groupChatReceived"),
  UserActivity("userActivity", "userActivity"),
  QuestActivity("questActivity", "questActivity");

  private final String queryValue;
  private final String jsonValue;

  WebHookType(String queryValue, String jsonValue){
    this.queryValue = queryValue;
    this.jsonValue = jsonValue;
  }

  public String getQueryValue(){
    return queryValue;
  }

  @JsonValue
  public String getJsonValue(){
    return jsonValue;
  }
}
