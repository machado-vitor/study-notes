"""Linear Regression using scikit-learn.

Uses sklearn's LinearRegression on a synthetic dataset to:
  1. Generate random data with a known linear relationship
  2. Fit the model
  3. Evaluate with slope/intercept
  4. Plot the regression line over the data points
"""

import numpy as np
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression


def main():
    # generate dataset
    np.random.seed(42)
    X = np.random.rand(50, 1) * 100
    Y = 3.5 * X + np.random.randn(50, 1) * 20

    # fit model
    model = LinearRegression()
    model.fit(X, Y)

    Y_pred = model.predict(X)

    # results
    print(f"  Slope (coef):  {model.coef_[0][0]:.4f}")
    print(f"  Intercept:     {model.intercept_[0]:.4f}\n")

    plt.figure(figsize=(8, 6))
    plt.scatter(X, Y, color="blue", label="Data Points")
    plt.plot(X, Y_pred, color="red", linewidth=2, label="Regression Line")
    plt.title("Linear Regression on Random Dataset")
    plt.xlabel("X")
    plt.ylabel("Y")
    plt.legend()
    plt.grid(True)
    plt.show()


if __name__ == "__main__":
    main()
