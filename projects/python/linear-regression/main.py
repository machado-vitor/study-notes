"""Linear Regression from scratch — no external libraries.

Implements:
  1. A simple dataset (house area -> price)
  2. Cost function (MSE)
  3. Gradient Descent to learn weight (m) and bias (b)
  4. Prediction on new inputs
"""


def normalize(data: list[float]) -> tuple[list[float], float, float]:
    """Normalize values to [0, 1] range. Returns (normalized, min, max)."""
    min_val = min(data)
    max_val = max(data)
    range_val = max_val - min_val
    normalized = [(x - min_val) / range_val for x in data]
    return normalized, min_val, max_val


def predict(x: list[float], weight: float, bias: float) -> list[float]:
    """Predict y = m*x + b."""
    return [weight * xi + bias for xi in x]


def mse(actual: list[float], predicted: list[float]) -> float:
    """Mean Squared Error: (1/n) * Σ(actual - predicted)²."""
    n = len(actual)
    return sum((a - p) ** 2 for a, p in zip(actual, predicted)) / n


def train(
    x: list[float],
    y: list[float],
    learning_rate: float,
    epochs: int,
) -> tuple[float, float]:
    """Train using Gradient Descent. Returns (weight, bias)."""
    weight = 0.0
    bias = 0.0
    n = len(x)

    print(f"=== Training ({epochs} epochs, lr={learning_rate}) ===\n")

    for epoch in range(epochs):
        predictions = predict(x, weight, bias)
        cost = mse(y, predictions)

        # Gradients: partial derivatives of MSE w.r.t. weight and bias
        #   ∂MSE/∂m = (-2/n) * Σ x_i * (y_i - predicted_i)
        #   ∂MSE/∂b = (-2/n) * Σ (y_i - predicted_i)
        grad_w = sum(-2.0 * xi * (yi - pi) for xi, yi, pi in zip(x, y, predictions)) / n
        grad_b = sum(-2.0 * (yi - pi) for yi, pi in zip(y, predictions)) / n

        # Update parameters
        weight -= learning_rate * grad_w
        bias -= learning_rate * grad_b

        # Log progress at key points
        if epoch < 5 or epoch % 200 == 0 or epoch == epochs - 1:
            print(f"  Epoch {epoch:>4} | Cost: {cost:.8f} | w: {weight:.6f} | b: {bias:.6f}")

    print()
    return weight, bias


def main():
    # --- Dataset: house area (sqft) -> price ($k) ---
    areas = [600.0, 800.0, 1000.0, 1200.0, 1400.0, 1600.0, 1800.0, 2000.0]
    prices = [150.0, 200.0, 245.0, 300.0, 360.0, 400.0, 440.0, 500.0]

    # Normalize features to [0, 1] — helps gradient descent converge faster
    areas_norm, area_min, area_max = normalize(areas)
    prices_norm, price_min, price_max = normalize(prices)

    # --- Hyperparameters ---
    learning_rate = 0.1
    epochs = 1000

    # --- Train ---
    weight, bias = train(areas_norm, prices_norm, learning_rate, epochs)

    # --- Results ---
    print("=== Training Complete ===\n")
    print("Learned parameters (normalized space):")
    print(f"  weight (m) = {weight:.6f}")
    print(f"  bias   (b) = {bias:.6f}\n")

    # Denormalize to get real-world equation:
    # y_norm = m * x_norm + b
    # price = m * ((area - area_min) / (area_max - area_min)) * (price_max - price_min) + b * (price_max - price_min) + price_min
    real_weight = weight * (price_max - price_min) / (area_max - area_min)
    real_bias = bias * (price_max - price_min) + price_min - real_weight * area_min
    print("Real-world equation:")
    print(f"  price = {real_weight:.2f} * area + {real_bias:.2f}\n")

    # --- Predictions ---
    print("=== Predictions ===\n")
    test_areas = [700.0, 1100.0, 1500.0, 2200.0]
    for area in test_areas:
        price = real_weight * area + real_bias
        print(f"  Area: {area:>6.0f} sqft  ->  Price: ${price:>8.2f}k")

    # --- Show training data fit ---
    print("\n=== Training Data Fit ===\n")
    print(f"  {'Area':>10} {'Actual':>12} {'Predicted':>12} {'Error':>10}")
    print(f"  {'-' * 48}")
    total_error = 0.0
    for area, actual_price in zip(areas, prices):
        predicted_price = real_weight * area + real_bias
        error = abs(actual_price - predicted_price)
        total_error += error
        print(f"  {area:>10.0f} {actual_price:>11.2f}k {predicted_price:>11.2f}k {error:>9.2f}k")
    print(f"  {'-' * 48}")
    print(f"  Average absolute error: {total_error / len(areas):.2f}k")


if __name__ == "__main__":
    main()
