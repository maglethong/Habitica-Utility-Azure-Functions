package com.maglethong.habitica.utility.core.habiticautility.api;

import com.maglethong.habitica.utility.core.habitica.api.WebhookActivity;

public interface IHabiticaUtilityService {

  /**
   * TODO
   *
   * @param activity the body sent with the web hook
   */
  void treatTaskUpdatedWebHookActivity(WebhookActivity activity);
}
