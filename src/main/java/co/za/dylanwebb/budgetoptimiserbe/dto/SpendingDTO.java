package co.za.dylanwebb.budgetoptimiserbe.dto;

import java.time.LocalDateTime;

public class SpendingDTO {
  private LocalDateTime dateTime;
  private float amount;
  private Long categoryId; // Pass the ID of the category this spending belongs to

  public SpendingDTO() {}

  public SpendingDTO(LocalDateTime dateTime, float amount, Long categoryId) {
    this.dateTime = dateTime;
    this.amount = amount;
    this.categoryId = categoryId;
  }

  // Getters and Setters
  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public float getAmount() {
    return amount;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }
}
