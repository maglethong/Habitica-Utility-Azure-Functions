package com.maglethong.habitica.utility.core.habitica.api;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Attribute {
  Strength("str"),
  Intelligence("int"),
  Perception("per"),
  Constitution("con");

  private final String jsonValue;

  Attribute(String jsonValue) {
    this.jsonValue = jsonValue;
  }

  @JsonValue
  public String getJsonValue() {
    return jsonValue;
  }
}
