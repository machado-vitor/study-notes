## AI - Linear Regression

Linear Regression is one of the simplest and most fundamental algorithms in Machine Learning. It models the relationship between a **dependent variable** (target) and one or more **independent variables** (features) by fitting a straight line through the data.

### Core Idea

Given data points, find the line that **best fits** them — minimizing the distance between predicted and actual values.

```
y = mx + b
```

- **y** — predicted value (output)
- **x** — input feature
- **m** — slope (weight) — how much y changes when x changes
- **b** — intercept (bias) — value of y when x = 0

With multiple features, it generalizes to:

```
y = w1*x1 + w2*x2 + ... + wn*xn + b
```

### How It Learns — Cost Function

The model needs a way to measure **how wrong** its predictions are. This is done via a **cost function**, most commonly **Mean Squared Error (MSE)**:

```
MSE = (1/n) * Σ (y_actual - y_predicted)²
```

- Squaring penalizes large errors more heavily.
- The goal: **minimize MSE** — make predictions as close as possible to actual values.

### How It Optimizes — Gradient Descent

Gradient Descent is the algorithm that **adjusts weights (m) and bias (b)** to minimize the cost function.

1. Start with random values for m and b.
2. Calculate the cost (MSE).
3. Compute the **gradient** (slope of the cost function) — tells which direction to adjust.
4. Update m and b by a small step in the direction that reduces cost.
5. Repeat until convergence (cost stops decreasing significantly).

```
m = m - learning_rate * (∂MSE/∂m)
b = b - learning_rate * (∂MSE/∂b)
```

**Learning Rate** — controls the step size. Too large: overshoots. Too small: converges very slowly.

### Visual Intuition

```
  y
  |        *
  |      * /
  |    *  /        <- best fit line
  |   * /
  |  * /
  | */
  |/___________  x
```

Each `*` is a data point. The line is positioned to minimize the total squared distance from all points to the line.

### Simple vs Multiple Linear Regression

| Type | Features | Equation |
|------|----------|----------|
| Simple | 1 input | y = mx + b |
| Multiple | n inputs | y = w1x1 + w2x2 + ... + b |

### Assumptions

Linear Regression works well when:
1. **Linearity** — the relationship between features and target is approximately linear.
2. **Independence** — observations are independent of each other.
3. **Homoscedasticity** — the variance of errors is constant across predictions.
4. **No multicollinearity** — features are not highly correlated with each other (for multiple regression).

### When to Use

- Predicting continuous values (price, temperature, sales).
- Understanding the impact of each feature on the outcome.
- When the relationship between variables is roughly linear.

### When NOT to Use

- Target is categorical (use Logistic Regression or classifiers instead).
- Relationship is highly non-linear (consider polynomial regression, decision trees, or neural networks).
- Data has many outliers (they heavily distort the line).

### Practical Example — Predicting House Prices

```
Features (x):        Target (y):
- area (sqft)   -->  price ($)
- num_bedrooms
- distance_to_center

Model learns:
price = 150*area + 20000*bedrooms - 5000*distance + 50000
```

The weights tell you: each extra sqft adds ~$150, each bedroom adds ~$20k, and each km from center reduces price by ~$5k.

### Evaluation Metrics

| Metric | What it measures |
|--------|-----------------|
| **MSE** | Average squared error — penalizes large errors |
| **RMSE** | Square root of MSE — same unit as target |
| **MAE** | Average absolute error — more robust to outliers |
| **R²** | How much variance the model explains (0 to 1, higher = better) |

### Key Takeaways

- Linear Regression = finding the best straight line through data.
- It learns by minimizing prediction error using Gradient Descent.
- Simple, interpretable, and a foundation for understanding more complex models.
- Always check if linearity assumptions hold before relying on it.