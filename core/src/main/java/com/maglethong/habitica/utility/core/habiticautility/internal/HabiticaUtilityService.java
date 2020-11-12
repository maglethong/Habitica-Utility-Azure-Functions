package com.maglethong.habitica.utility.core.habiticautility.internal;

import com.google.inject.Inject;
import com.maglethong.habitica.utility.core.habitica.api.Task;
import com.maglethong.habitica.utility.core.habitica.api.WebHookActivityType;
import com.maglethong.habitica.utility.core.habitica.api.WebHookType;
import com.maglethong.habitica.utility.core.habitica.api.WebhookActivity;
import com.maglethong.habitica.utility.core.habitica.internal.HabiticaClientService;
import com.maglethong.habitica.utility.core.habiticautility.api.IHabiticaUtilityService;
import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HabiticaUtilityService implements IHabiticaUtilityService {
  private static final Logger LOGGER = LoggerFactory.getLogger(HabiticaUtilityService.class);

  private final HabiticaClientService habiticaClientService;

  @Inject
  @Autowired
  public HabiticaUtilityService(HabiticaClientService habiticaClientService) {
    this.habiticaClientService = habiticaClientService;
  }

  @Override
  public void treatTaskUpdatedWebHookActivity(WebhookActivity activity) {
    LOGGER.debug("HabiticaUtilityService#treatTaskUpdatedWebHookActivity({}) called", activity);
    if (activity == null) { // TODO -> To arg validator
      throw new RuntimeException(); // TODO
    }

    if (activity.getType() != WebHookType.TaskActivity) { // TODO -> To arg validator
      throw new RuntimeException(); // TODO
    }

    if (activity.getActivityType() == WebHookActivityType.TaskScored) {
      if (activity.getTask() == null) {
        throw new RuntimeException(); // TODO Treat
      }
      if (activity.getTask().getText() != null && activity.getTask().getText().toLowerCase().equals("Bank")) {
        processBankTaskPressed(activity.getTask());
      }
    }
  }

  /**
   * Updates the Bank Reward Task notes.
   * <p/>
   * If the Task notes contain the following value:
   * {@code Balance `<integer value>`}
   * it will be updated. otherwise it will be created containing the current balance.
   * <p/>
   * If the Task notes does not contain the following value:
   * {@code Tax `<integer value>`}
   * it will be created containing 0 as value.
   * <p/>
   * The {@code Tax} value is subtracted from the Reward cost before being added to the current balance.
   *
   * @param task The task being updated
   */
  void processBankTaskPressed(Task task){ // TODO -> Test
    Task updateValues = new Task();

    String note_s = task.getNotes();
    String[] notes = note_s.split("\n");

    int tax = 0;
    int balance = task.getTaskValue();
    int origBalance = 0;
    String taskId = task.getId();

    // Get Tax value from notes
    Optional<String> depositTax_s = Arrays
        .stream(notes)
        .filter(s -> s.toLowerCase().contains("deposit tax"))
        .findFirst();
    if (depositTax_s.isPresent()) {
      String value = depositTax_s.get();
      tax = Integer.parseInt(value.substring(value.indexOf('`'), value.lastIndexOf('`')));
    } else {
      if (!note_s.isEmpty()) {
        note_s += "\n\n";
      }
      note_s += "Tax: `0`";
    }

    // Update/Create Balance in notes
    Optional<String> balance_s = Arrays
        .stream(notes)
        .filter(s -> s.toLowerCase().contains("balance"))
        .findFirst();
    if (balance_s.isPresent()) {
      String value = balance_s.get();
      origBalance = Integer.parseInt(value.substring(value.indexOf('`'), value.lastIndexOf('`')));
      balance += origBalance - tax;
      note_s = note_s.replace(value, "Balance: `" + balance + "`");
      updateValues.setNotes(note_s);
    } else {
      balance -= tax;
      note_s += "Balance: `" + balance + "`";
      updateValues.setNotes(note_s);
    }

    LOGGER.info("Updating Bank balance (TaskId: {}) from {} to {} (tax paid was {}).", taskId, origBalance, balance, tax);
    habiticaClientService.updateTask(taskId, updateValues);
  }
}
