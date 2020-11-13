package com.maglethong.habitica.utility.core.habitica.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task extends Object implements Cloneable {
  private String id;
  @JsonProperty("type")
  private TaskType taskType;
  private Attribute attribute;
  private String notes;
  @JsonProperty("tags")
  private List<String> tagIds;
  @JsonProperty("value")
  private Short taskValue;
  private Short priority;
  private Date createdAt;
  private Date updatedAt;
  private String text;


  public String getId() {
    return id;
  }

  public Task setId(String id) {
    this.id = id;
    return this;
  }

  public TaskType getTaskType() {
    return taskType;
  }

  public Task setTaskType(TaskType taskType) {
    this.taskType = taskType;
    return this;
  }

  public Attribute getAttribute() {
    return attribute;
  }

  public Task setAttribute(Attribute attribute) {
    this.attribute = attribute;
    return this;
  }

  public String getNotes() {
    return notes;
  }

  public Task setNotes(String notes) {
    this.notes = notes;
    return this;
  }

  public List<String> getTagIds() {
    return tagIds;
  }

  public Task setTagIds(List<String> tagIds) {
    this.tagIds = tagIds;
    return this;
  }

  public Short getTaskValue() {
    return taskValue;
  }

  public Task setTaskValue(Short taskValue) {
    this.taskValue = taskValue;
    return this;
  }

  public Short getPriority() {
    return priority;
  }

  public Task setPriority(Short priority) {
    this.priority = priority;
    return this;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public Task setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public Task setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public String getText() {
    return text;
  }

  public Task setText(String text) {
    this.text = text;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Task task = (Task) o;
    return Objects.equals(id, task.id)
        && taskType == task.taskType
        && attribute == task.attribute
        && Objects.equals(notes, task.notes)
        && Objects.equals(tagIds, task.tagIds)
        && Objects.equals(taskValue, task.taskValue)
        && Objects.equals(priority, task.priority)
        && Objects.equals(createdAt, task.createdAt)
        && Objects.equals(updatedAt, task.updatedAt)
        && Objects.equals(text, task.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, taskType, attribute, notes, tagIds, taskValue, priority, createdAt, updatedAt, text);
  }

  @Override
  public Object clone() {
    Task task;
    try {
      task = (Task) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e); // TODO
    }
    task
        .setTagIds(new ArrayList<>(this.tagIds))
        .setCreatedAt((Date) this.getCreatedAt().clone())
        .setUpdatedAt((Date) this.getUpdatedAt().clone());
    return task;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Task{");
    sb.append("id='").append(id).append('\'');
    sb.append(", taskType=").append(taskType);
    sb.append(", attribute=").append(attribute);
    sb.append(", notes='").append(notes).append('\'');
    sb.append(", tagIds=").append(tagIds);
    sb.append(", taskValue=").append(taskValue);
    sb.append(", priority=").append(priority);
    sb.append(", createdAt=").append(createdAt);
    sb.append(", updatedAt=").append(updatedAt);
    sb.append(", text='").append(text).append('\'');
    sb.append('}');
    return sb.toString();
  }
}