package com.maglethong.habitica.utility.core.habitica.api;

import java.util.Collection;
import java.util.Date;

public interface IHabiticaClientService {

  /**
   * Gets a list with the users tasks.
   *
   * @param filterByDate [Optional] if provided, will filter the result by due date //TODO -> How is it filtered?
   * @param filterByType [Optional] if provided, result will only include tasks of given type
   * @return a collection of the retrieved tasks for the logged user
   */
  Collection<Task> getTasks(Date filterByDate, TaskType filterByType);

  /**
   * Update a task with new values.
   *
   * @param taskId The id of the task to update
   * @param task The new values to use for the task. the ID and {@code null} values will be ignored.
   * @return true if the update succeeded
   */
  boolean updateTask(String taskId, Task task);
}
