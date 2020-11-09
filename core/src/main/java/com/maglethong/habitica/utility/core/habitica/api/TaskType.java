package com.maglethong.habitica.utility.core.habitica.api;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskType {
  Habits("habits", "habit"),
  Dailies("dailys", "daily"),
  Todos("todos", "todo"),
  Reward("rewards", "reward"),
  CompletedTodos("completedTodos", "completedTodo");

  private final String queryValue;
  private final String jsonValue;

  TaskType(String queryValue, String jsonValue){
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
