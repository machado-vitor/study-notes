/// Linear Regression from scratch — no external libraries.
///
/// Implements:
///   1. A simple dataset (house area -> price)
///   2. Cost function (MSE)
///   3. Gradient Descent to learn weight (m) and bias (b)
///   4. Prediction on new inputs

fn main() {
    // --- Dataset: house area (sqft) -> price ($k) ---
    let areas = vec![600.0, 800.0, 1000.0, 1200.0, 1400.0, 1600.0, 1800.0, 2000.0];
    let prices = vec![150.0, 200.0, 245.0, 300.0, 360.0, 400.0, 440.0, 500.0];

    // Normalize features to [0, 1] — helps gradient descent converge faster
    let (areas_norm, area_min, area_max) = normalize(&areas);
    let (prices_norm, price_min, price_max) = normalize(&prices);

    // --- Hyperparameters ---
    let learning_rate = 0.1;
    let epochs = 1000;

    // --- Train ---
    let (weight, bias) = train(&areas_norm, &prices_norm, learning_rate, epochs);

    // --- Results ---
    println!("=== Training Complete ===\n");
    println!("Learned parameters (normalized space):");
    println!("  weight (m) = {weight:.6}");
    println!("  bias   (b) = {bias:.6}\n");

    // Denormalize to get real-world equation:
    // y_norm = m * x_norm + b
    // price = m * ((area - area_min) / (area_max - area_min)) * (price_max - price_min) + b * (price_max - price_min) + price_min
    let real_weight = weight * (price_max - price_min) / (area_max - area_min);
    let real_bias = bias * (price_max - price_min) + price_min - real_weight * area_min;
    println!("Real-world equation:");
    println!("  price = {real_weight:.2} * area + {real_bias:.2}\n");

    // --- Predictions ---
    println!("=== Predictions ===\n");
    let test_areas = vec![700.0, 1100.0, 1500.0, 2200.0];
    for area in &test_areas {
        let price = real_weight * area + real_bias;
        println!("  Area: {area:>6.0} sqft  ->  Price: ${price:>8.2}k");
    }

    // --- Show training data fit ---
    println!("\n=== Training Data Fit ===\n");
    println!("  {:>10} {:>12} {:>12} {:>10}", "Area", "Actual", "Predicted", "Error");
    println!("  {}", "-".repeat(48));
    let mut total_error = 0.0;
    for i in 0..areas.len() {
        let predicted = real_weight * areas[i] + real_bias;
        let error = (prices[i] - predicted).abs();
        total_error += error;
        println!(
            "  {:>10.0} {:>11.2}k {:>11.2}k {:>9.2}k",
            areas[i], prices[i], predicted, error
        );
    }
    println!("  {}", "-".repeat(48));
    println!("  Average absolute error: {:.2}k", total_error / areas.len() as f64);
}

/// Normalize values to [0, 1] range. Returns (normalized, min, max).
fn normalize(data: &[f64]) -> (Vec<f64>, f64, f64) {
    let min = data.iter().cloned().fold(f64::INFINITY, f64::min);
    let max = data.iter().cloned().fold(f64::NEG_INFINITY, f64::max);
    let range = max - min;
    let normalized = data.iter().map(|x| (x - min) / range).collect();
    (normalized, min, max)
}

/// Predict y = m*x + b
fn predict(x: &[f64], weight: f64, bias: f64) -> Vec<f64> {
    x.iter().map(|xi| weight * xi + bias).collect()
}

/// Mean Squared Error: (1/n) * Σ(actual - predicted)²
fn mse(actual: &[f64], predicted: &[f64]) -> f64 {
    let n = actual.len() as f64;
    actual
        .iter()
        .zip(predicted.iter())
        .map(|(a, p)| (a - p).powi(2))
        .sum::<f64>()
        / n
}

/// Train using Gradient Descent. Returns (weight, bias).
fn train(x: &[f64], y: &[f64], learning_rate: f64, epochs: usize) -> (f64, f64) {
    let mut weight = 0.0;
    let mut bias = 0.0;
    let n = x.len() as f64;

    println!("=== Training ({epochs} epochs, lr={learning_rate}) ===\n");

    for epoch in 0..epochs {
        let predictions = predict(x, weight, bias);
        let cost = mse(y, &predictions);

        // Gradients: partial derivatives of MSE w.r.t. weight and bias
        //   ∂MSE/∂m = (-2/n) * Σ x_i * (y_i - predicted_i)
        //   ∂MSE/∂b = (-2/n) * Σ (y_i - predicted_i)
        let mut grad_w = 0.0;
        let mut grad_b = 0.0;
        for i in 0..x.len() {
            let error = y[i] - predictions[i];
            grad_w += -2.0 * x[i] * error;
            grad_b += -2.0 * error;
        }
        grad_w /= n;
        grad_b /= n;

        // Update parameters
        weight -= learning_rate * grad_w;
        bias -= learning_rate * grad_b;

        // Log progress at key points
        if epoch < 5 || epoch % 200 == 0 || epoch == epochs - 1 {
            println!("  Epoch {epoch:>4} | Cost: {cost:.8} | w: {weight:.6} | b: {bias:.6}");
        }
    }

    println!();
    (weight, bias)
}