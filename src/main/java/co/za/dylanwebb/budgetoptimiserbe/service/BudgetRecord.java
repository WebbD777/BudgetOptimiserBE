package co.za.dylanwebb.budgetoptimiserbe.service;

public class BudgetRecord{
    private String catName;
    private float budget;
    private long id;

    public BudgetRecord(String catName, float budget, long id) {
        this.catName = catName;
        this.budget = budget;
        this.id = id;
    }

    public String getCatName() {
        return catName;
    }

    public float getBudget() {
        return budget;
    }
    public long getId() {
        return id;
    }
    
}
