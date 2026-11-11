import numpy as np
import pandas as pd

# ---------------------------------------------------------
# 1. Raw Historical Data (Easily append new months here!)
# ---------------------------------------------------------
raw_data = {
    'Month':       ['Mar-Apr', 'Apr-May', 'May-Jun', 'Jun-Jul', 'Jul-Aug', 'Aug-Sep', 'Sep-Oct'],
    'Electricity': [1000,      1200,      1000,      1400,      1270,      1300,      1400],
    'Fuel':        [405,       200,       200,       700,       500,       800,       600],
    'Food':        [296.11,    737.65,    685.24,    992.05,    878.21,    988.19,    1109.09],
    'Household':   [91.15,     20.99,     254.45,    209.20,    118.56,    179.39,    138.68],
    'Data':        [45,        59,        59,        59,        109,       59,        59]
}

df_history = pd.DataFrame(raw_data)

# ---------------------------------------------------------
# 2. Automatically Generate Empirical Distributions
# ---------------------------------------------------------
empirical_data = {}
expense_categories = ['Electricity', 'Fuel', 'Food', 'Household', 'Data']

for col in expense_categories:
    # Count frequencies of each unique value in the column
    value_counts = df_history[col].value_counts().sort_index()
    
    empirical_data[col] = {
        'values': value_counts.index.tolist(),
        'probs': (value_counts / len(df_history)).tolist()
    }

# ---------------------------------------------------------
# 3. Allocated Target Budgets
# ---------------------------------------------------------
budgets = {
    'Electricity': 1000.0,
    'Fuel':        1000.0,
    'Food':        600.0,
    'Household':   550.0,
    'Data':        200.0
}
total_budget = sum(budgets.values())

# ---------------------------------------------------------
# 4. Run Monte Carlo Simulation (100,000 Iterations)
# ---------------------------------------------------------
num_trials = 100_000
np.random.seed(42)

sim_data = {}
for category, dist in empirical_data.items():
    sim_data[category] = np.random.choice(dist['values'], size=num_trials, p=dist['probs'])

df_sim = pd.DataFrame(sim_data)
df_sim['Total'] = df_sim.sum(axis=1)

# ---------------------------------------------------------
# 5. Output Overrun Risk Dashboard
# ---------------------------------------------------------
results = []
for category in expense_categories + ['Total']:
    bgt = total_budget if category == 'Total' else budgets[category]
    expenses = df_sim[category]
    
    overrun_prob = (expenses > bgt).mean()
    mean_exp = expenses.mean()
    p90_exp = np.percentile(expenses, 90)
    
    risk_label = "HIGH RISK" if overrun_prob > 0.5 else ("MODERATE RISK" if overrun_prob > 0.1 else "SAFE")
    
    results.append({
        'Category': category,
        'Budget': bgt,
        'Simulated Mean': mean_exp,
        '90th Percentile (P90)': p90_exp,
        'Overrun Probability': f"{overrun_prob:.1%}",
        'Risk Assessment': risk_label
    })

df_dashboard = pd.DataFrame(results)
print(df_dashboard.to_string(index=False))