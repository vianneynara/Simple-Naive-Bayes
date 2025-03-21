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
	public Map<String, Double> predict(Map<String, String> X, String yFeature) {
		Map<String, Double> predictions = new HashMap<>();

		return predictions;
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
		int eventOccurrence = yCorrelations.get(yValue).get(xFeature).get(xValue);
		System.out.printf("Computing: (%s + 1) / (%d + %d) %n", eventOccurrence, yValueCount, xDistinctSize);
		return (eventOccurrence + 1) / ((yValueCount + xDistinctSize) * 1.0);
	}

	/**
	 * Calculates probabilities for every value in a feature/column.
	 * Uses the formula: P(X_i) = n_i/N
	 *
	 * @param feature The feature to compute.
	 */
	private Map<String, Double> computeProbabilities(String feature) {
		Map<String, Double> probabilities = new HashMap<>();

		return probabilities;
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
