package com.maglethong.habitica.utility.core.habitica.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HabiticaRequestResult<T> {
  public T data;
  public boolean success;
}
