package co.za.dylanwebb.budgetoptimiserbe.dto;

import java.util.List;
import java.util.Map;

public class MonteCarloDTO {

    private Map<String, Map<String, Double>> historicalData;
    private Map<String, Double> budgets;

    public MonteCarloDTO(
            Map<String, Map<String, Double>> historicalData,
            Map<String, Double> budgets) {

        this.historicalData = historicalData;
        this.budgets = budgets;
    }

    public Map<String, Map<String, Double>> getHistoricalData() {
        return historicalData;
    }

    public Map<String, Double> getBudgets() {
        return budgets;
    }
}
