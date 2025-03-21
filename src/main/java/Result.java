import java.util.HashMap;
import java.util.Map;

public class Result {
	/**
	 * Storing the occurrence probability of X given y.
	 */
	Map<String, Map<String, Double>> probabilities;
	/**
	 * Storing the predictions probabilities, consists of classifications and its probabilities.
	 */
	Map<String, Double> predictions;
	/**
	 * Final prediction.
	 */
	String classification;
	/**
	 * Storing the normalized predictions.
	 * */
	Map<String, Double> normalizedPredictions;

	public Result(Map<String, Map<String, Double>> probabilities, Map<String, Double> predictions) {
		this.probabilities = probabilities;
		this.predictions = predictions;
		this.classification = NaiveBayes.getHighestProbability(this.predictions);
		this.normalizedPredictions = normalize(this.predictions);
	}

	public Map<String, Map<String, Double>> probabilities() {
		return probabilities;
	}

	public Map<String, Double> predictions() {
		return predictions;
	}

	public String classification() {
		return classification;
	}

	public Map<String, Double> normalizedPredictions() {
		return normalizedPredictions;
	}

	public static Result of(
		Map<String, Map<String, Double>> probabilities,
		Map<String, Double> predictions
	) {
		return new Result(probabilities, predictions);
	}

	private Map<String, Double> normalize(Map<String, Double> predictions) {
		Map<String, Double> normalizedPredictions = new HashMap<>();
		double sum = predictions.values().stream().mapToDouble(Double::doubleValue).sum();
		for (var predEntry : predictions.entrySet()) {
			normalizedPredictions.put(predEntry.getKey(), predEntry.getValue() / sum);
		}
		return normalizedPredictions;
	}
}