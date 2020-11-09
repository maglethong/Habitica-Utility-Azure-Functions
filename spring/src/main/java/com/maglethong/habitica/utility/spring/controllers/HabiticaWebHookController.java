package com.maglethong.habitica.utility.spring.controllers;

import com.maglethong.habitica.utility.core.habitica.api.IHabiticaClientService;
import com.maglethong.habitica.utility.core.habitica.api.TaskType;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/habitica/webhook")
public class HabiticaWebHookController {

  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaWebHookController.class);

  private final IHabiticaClientService habiticaService;

  @Value("${com.maglethongspirr.habitica.test.prop:none}")
  private String testProp;

  @Autowired
  public HabiticaWebHookController(IHabiticaClientService habiticaService) {
    this.habiticaService = habiticaService;
  }

  /**
   * Some Method description.
   *
   * @return some return value
   * @param body some description
   * @param param some description
   * @param var some description
   */
  @PostMapping("/{var}")
  public Object someMethod(@RequestBody String body,
                           @RequestParam(required = false) Long param,
                           @PathVariable long var) {
    LOGGER.info(testProp);
    return habiticaService.getTasks(new Date(), TaskType.Reward);
  }
}