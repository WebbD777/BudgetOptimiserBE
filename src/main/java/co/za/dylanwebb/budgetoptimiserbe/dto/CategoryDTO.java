package co.za.dylanwebb.budgetoptimiserbe.dto;

import co.za.dylanwebb.budgetoptimiserbe.model.Category;

public class CategoryDTO {
  private String category;
  private float budget;
  private Category.CostType costType;

  public CategoryDTO() {}

  public CategoryDTO(String category, float budget, Category.CostType costType) {
    this.category = category;
    this.budget = budget;
    this.costType = costType;
  }

  // Getters and Setters
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public float getBudget() {
    return budget;
  }

  public void setBudget(float budget) {
    this.budget = budget;
  }

  public Category.CostType getCostType() {
    return costType;
  }

  public void setCostType(Category.CostType costType) {
    this.costType = costType;
  }
}
