import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Naive Bayes probability calculator for a given Dataset.
 * Equipped with Laplacian correction.
 *
 * @author narwa+
 */
public class NaiveBayes {

	private Dataset d;

	public NaiveBayes(Dataset dataset) {
		this.d = dataset;
	}

	public Result predict(Map<String, String> X, String yFeature, boolean printStats) {
		Result result = infer(X, yFeature);

		if (printStats) {
			StringBuilder sb = new StringBuilder();
			sb.append("=".repeat(50)).append("\n");
			sb.append("Singular probability P(X_i|C_i):").append("\n");
			sb.append("Given X: ").append(X).append("\n");
			result.probabilities().forEach((yValue, xProbabilities) -> {
				sb.append("\t").append(yValue).append(":\n");
				xProbabilities.forEach((xFeature, xProbability) -> {
					sb.append("\t\t").append(String.format("%-20s", xFeature)).append(": ").append(String.format("%.4f", xProbability))
						.append("\n");
				});
			});
			sb.append("-".repeat(50)).append("\n");
			sb.append("Predictions:").append("\n");
			result.predictions().forEach((yValue, yProbability) -> {
				sb.append("\t\t").append(String.format("%-20s", yValue)).append(": ").append(String.format("%.4f", yProbability))
					.append(String.format(" [%.2f%%]", result.normalizedPredictions().get(yValue) * 100))
					.append("\n");
			});
			sb.append("-".repeat(50)).append("\n");
			System.out.print(sb.toString());
		}

		return result;
	}

	/**
	 * Get the highest probability outcome of y (class/target feature) given X (features).
	 *
	 * @param X        Input values that matches `len(N) - 1` length of the dataset.
	 *                 The Key is the feature, the value is the value of the input. Example:
	 *                 <pre>
	 *                      {@code
	 *                        "age" : "31...40"
	 *                        "income" : "low"
	 *                        "student" : "no"
	 *                        "credit_rating" : "fair"
	 *                      }
	 *                 </pre>
	 * @param yFeature Target or classification feature. Example: {@code "buys_computer"}
	 * @return Value-Probability map in y.
	 */
	public Result infer(Map<String, String> X, String yFeature) {
		if (X == null || X.size() != d.metadata().size() - 1) {
			throw new IllegalArgumentException("X should be the same size as the dataset's features - 1.");
		}

		// probability tracker for each feature
		Map<String, Map<String, Double>> probabilities = new HashMap<>();
		// (outcome - probability) pair
		Map<String, Double> predictions = new HashMap<>();
		var yCorrelations = readCorrelations(yFeature);

		for (var yValue : d.metadata().get(yFeature).keySet()) {
			/* computing probabilities for every event for every X feature given y */
			probabilities.put(yValue, new HashMap<>());
			for (int i = 0; i < d.headers().size(); i++) {
				if (d.headers().get(i).equals(yFeature)) {
					continue;
				}

				String xFeature = d.headers().get(i);
//				System.out.printf("Computing Probability for (yCorrelation, %s, %s, %s, %s)%n", xFeature, X.get(xFeature), yFeature, yValue);
				double eventProbability = computeProbability(yCorrelations, xFeature, X.get(xFeature), yFeature, yValue);
				probabilities.get(yValue).put(xFeature, eventProbability);
			}

			/* compute predictions */
			double totalProbability = 1.0;
			for (var probability : probabilities.get(yValue).values()) {
				totalProbability *= probability;
			}
			double yProbability = d.metadata().get(yFeature).get(yValue) / (d.data().size() * 1.0);
			predictions.put(yValue, totalProbability * yProbability);
		}

		return Result.of(probabilities, predictions);
	}

	/**
	 * Calculates probability from the dataset given xFeature's value of xValue and yFeature's value of yValue.
	 * <p>Uses the formula: {@code P(X | C_i)}</p>
	 * This also computes with Laplacian Correction to avoid zero probabilities.
	 *
	 * @param yCorrelations Correlation mapping
	 * @param xFeature      Input feature
	 * @param xValue        Input value
	 * @param yFeature      Condition feature
	 * @param yValue        Condition value
	 * @return double Probability of happening.
	 */
	public double computeProbability(
		Map<String, Map<String, Map<String, Integer>>> yCorrelations,
		String xFeature,
		String xValue,
		String yFeature,
		String yValue
	) {
		int yValueCount = d.metadata().get(yFeature).get(yValue);
		int xDistinctSize = d.metadata().get(xFeature).size();
		// (len(x_i) + 1) / (len(y_i) + |x|)
		// (x value's given y value's occurrences + 1) / (dataset size + x distinct size)
//		System.out.println("occurrence: " +
		// defaults not found with 0, else, you'll get NPE
		int eventOccurrence = yCorrelations.get(yValue).get(xFeature).getOrDefault(xValue, 0);
		double result = (eventOccurrence + 1) / ((yValueCount + xDistinctSize) * 1.0);
//		System.out.printf("Computing: (%s + 1) / (%d + %d) = %.4f %n", eventOccurrence, yValueCount, xDistinctSize, result);
		return result;
	}

	public Map<String, Map<String, Map<String, Integer>>> readCorrelations(String yFeature) {
		return readCorrelations(this.d, yFeature);
	}

	/**
	 * Correlation map is the occurence frequency of x (feature-value) given y (feature-value).
	 */
	public Map<String, Map<String, Map<String, Integer>>> readCorrelations(Dataset dataset, String yFeature) {
		/*
		 * yCorrelation is a map that relates occurrences of x (feature-value) given y (feature-value)'s values.
		 * It may seem "backwarded", but I like this approach better. Approach considerations:
		 *
		 * I have 2 ways of structuring this, either on the `Map<String, Map<String, Map<String, Integer>>>`,
		 * I put it such that the outer-most key is the y's values, for example if y feature has 2 discrete "yes"
		 * and "no" values, it will have 2 Entry sets with said keys, so that the values of the outer-most Map is
		 * x feature (column name), and the values are the frequency of each values x has given y's values.
		 *
		 * The other approach is having the outer-most key as x (column names), and the values of the outer-most's
		 * map will be x's values, then the innermost map's keys would be y's values, and its occurrences given y
		 * */
		Map<String, Map<String, Map<String, Integer>>> yCorrelations = new HashMap<>();

		int yIndex = -1;
		for (int i = 0; i < dataset.headers().size(); i++) {
			if (dataset.headers().get(i).equals(yFeature)) {
				yIndex = i;
				break;
			}
		}

		for (var value : dataset.metadata().get(yFeature).keySet()) {
			// put the y's values to correlate with other x features' values' occurrences
			yCorrelations.put(value, new HashMap<>());

			// insert X features as keys
			for (int i = 0; i < dataset.headers().size(); i++) {
				if (dataset.headers().get(i).equals(yFeature)) {
					continue;
				}
				yCorrelations.get(value).put(dataset.headers().get(i), new HashMap<>());
			}
		}

		// iterating each line
		for (List<String> row : dataset.data()) {
			// iterating each value in the line
			String currYValue = row.get(yIndex);
			for (int i = 0; i < dataset.headers().size(); i++) {
				if (i == yIndex) {
					continue;
				}

				String currXValue = row.get(i);
				String currXFeature = dataset.headers().get(i);
				int prevXOccurrence = yCorrelations.get(currYValue).get(currXFeature).getOrDefault(currXValue, 0);
				yCorrelations.get(currYValue).get(currXFeature).put(currXValue, ++prevXOccurrence);
			}
		}

		return yCorrelations;
	}

	public static String getHighestProbability(Map<String, Double> predictions) {
		double highestPrediction = 0.0;
		String highestPredictionClass = "";

		for (var entry : predictions.entrySet()) {
			if (entry.getValue() > highestPrediction) {
				highestPrediction = entry.getValue();
				highestPredictionClass = entry.getKey();
			}
		}

		return highestPredictionClass;
	}
}
