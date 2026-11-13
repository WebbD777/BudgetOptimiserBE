
import sys
import json
import numpy as np
import pandas as pd

# ---------------------------------------------------------
# 1. Receive data from Spring Boot
# ---------------------------------------------------------

input_data = json.load(sys.stdin)

budgets = input_data["budgets"]
historical_data = input_data["historicalData"]

if not budgets:
    raise ValueError("No budget data received.")

if not historical_data:
    raise ValueError("No historical spending data received.")

# ---------------------------------------------------------
# 2. Convert monthly historical spending into a DataFrame
# ---------------------------------------------------------

# Java sends:
# {
#     "2026-10": {"Food": 1000, "Transport": 300},
#     "2026-11": {"Food": 1200, "Transport": 400}
# }

df_history = pd.DataFrame.from_dict(
    historical_data,
    orient="index"
)

# Ensure every budget category exists
expense_categories = list(budgets.keys())

df_history = df_history.reindex(
    columns=expense_categories,
    fill_value=0
)

# Replace any missing values with zero
df_history = df_history.fillna(0)

# Convert values to numeric
df_history = df_history.astype(float)

# Sort months chronologically
df_history.index = pd.to_datetime(
    df_history.index,
    format="%Y-%m"
)

df_history = df_history.sort_index()

if df_history.empty:
    raise ValueError("Historical spending DataFrame is empty.")

number_of_months = len(df_history)

# ---------------------------------------------------------
# 3. Generate empirical distributions
# ---------------------------------------------------------

empirical_data = {}

for category in expense_categories:

    historical_values = df_history[category].values

    # Count the frequency of each monthly spending amount
    value_counts = pd.Series(historical_values).value_counts().sort_index()

    probabilities = value_counts / len(historical_values)

    empirical_data[category] = {
        "values": value_counts.index.to_numpy(dtype=float),
        "probs": probabilities.to_numpy(dtype=float)
    }

# ---------------------------------------------------------
# 4. Calculate total budget
# ---------------------------------------------------------

budgets = {
    category: float(amount)
    for category, amount in budgets.items()
}

total_budget = sum(budgets.values())

# ---------------------------------------------------------
# 5. Run Monte Carlo Simulation
# ---------------------------------------------------------

num_trials = 100_000

np.random.seed(42)

sim_data = {}

for category, dist in empirical_data.items():

    sim_data[category] = np.random.choice(
        dist["values"],
        size=num_trials,
        p=dist["probs"]
    )

df_sim = pd.DataFrame(sim_data)

# Calculate total monthly simulated spending
df_sim["Total"] = df_sim[expense_categories].sum(axis=1)

# ---------------------------------------------------------
# 6. Generate Overrun Risk Dashboard
# ---------------------------------------------------------

results = []

for category in expense_categories + ["Total"]:

    # Select the appropriate budget
    budget = (
        total_budget
        if category == "Total"
        else budgets[category]
    )

    expenses = df_sim[category]

    # Probability of exceeding budget
    overrun_prob = float((expenses > budget).mean())

    # Expected monthly spending
    mean_exp = float(expenses.mean())

    # 90th percentile spending
    p90_exp = float(np.percentile(expenses, 90))

    # Risk classification
    risk_label = (
        "HIGH RISK" if overrun_prob > 0.5
        else "MODERATE RISK" if overrun_prob > 0.1
        else "SAFE"
    )

    results.append({
        "Category": category,
        "Budget": round(budget, 2),
        "Simulated Mean": round(mean_exp, 2),
        "90th Percentile (P90)": round(p90_exp, 2),
        "Overrun Probability": round(overrun_prob, 4),
        "Risk Assessment": risk_label
    })

# ---------------------------------------------------------
# 7. Return JSON to Spring Boot
# ---------------------------------------------------------

output = {
    "numberOfHistoricalMonths": number_of_months,
    "totalBudget": round(total_budget, 2),
    "numberOfSimulations": num_trials,
    "results": results
}

print(json.dumps(output))
