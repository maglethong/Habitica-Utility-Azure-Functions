package com.maglethong.habitica.utility.core.habitica.api;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WebHookActivityType {
  QuestFinished("???", "questFinished"),
  QuestInvited("???", "questInvited"),
  TaskCreated("???", "created"),
  TaskScored("???", "scored"),
  TaskUpdated("???", "updated");

  private final String queryValue;
  private final String jsonValue;

  WebHookActivityType(String queryValue, String jsonValue){
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
