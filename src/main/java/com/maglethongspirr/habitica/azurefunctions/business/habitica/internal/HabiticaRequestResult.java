package com.maglethongspirr.habitica.azurefunctions.business.habitica.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HabiticaRequestResult<T> {
  public T data;
  public boolean success;
}
