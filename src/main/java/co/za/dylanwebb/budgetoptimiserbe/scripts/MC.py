
import sys
import json
import numpy as np
import pandas as pd

# ---------------------------------------------------------
# 1. Receive data from Spring Boot
# ---------------------------------------------------------

input_data = json.load(sys.stdin)

budgets = input_data["budgets"]
spendings = input_data["historicalData"]

# ---------------------------------------------------------
# 2. Convert historical spending into a DataFrame
# ---------------------------------------------------------

df_spending = pd.DataFrame(spendings)

if df_spending.empty:
    raise ValueError("No historical spending data received.")

df_spending["date"] = pd.to_datetime(df_spending["date"])

# Extract month and year
df_spending["Month"] = (
    df_spending["date"].dt.to_period("M").astype(str)
)

# Aggregate spending by month and category
df_history = df_spending.pivot_table(
    index="Month",
    columns="category",
    values="amount",
    aggfunc="sum",
    fill_value=0
)

# Ensure every budget category exists in historical data
expense_categories = list(budgets.keys())

df_history = df_history.reindex(
    columns=expense_categories,
    fill_value=0
)

df_history = df_history.reset_index()

# ---------------------------------------------------------
# 3. Generate empirical distributions
# ---------------------------------------------------------

empirical_data = {}

for col in expense_categories:

    value_counts = df_history[col].value_counts().sort_index()

    empirical_data[col] = {
        "values": value_counts.index.tolist(),
        "probs": (value_counts / len(df_history)).tolist()
    }

# ---------------------------------------------------------
# 4. Allocated target budgets
# ---------------------------------------------------------

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

df_sim["Total"] = df_sim.sum(axis=1)

# ---------------------------------------------------------
# 6. Output Overrun Risk Dashboard
# ---------------------------------------------------------

results = []

for category in expense_categories + ["Total"]:

    bgt = (
        total_budget
        if category == "Total"
        else budgets[category]
    )

    expenses = df_sim[category]

    overrun_prob = (expenses > bgt).mean()

    mean_exp = expenses.mean()

    p90_exp = np.percentile(expenses, 90)

    risk_label = (
        "HIGH RISK" if overrun_prob > 0.5
        else "MODERATE RISK" if overrun_prob > 0.1
        else "SAFE"
    )

    results.append({
        "Category": category,
        "Budget": round(float(bgt), 2),
        "Simulated Mean": round(float(mean_exp), 2),
        "90th Percentile (P90)": round(float(p90_exp), 2),
        "Overrun Probability": round(float(overrun_prob), 4),
        "Risk Assessment": risk_label
    })

df_dashboard = pd.DataFrame(results)

# Return JSON to Spring Boot
print(df_dashboard.to_json(orient="records"))
