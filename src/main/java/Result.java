import java.util.HashMap;
import java.util.Map;

public class Result {
	/**
	 * Storing the occurrence probability of X given y.
	 */
	Map<String, Map<String, Double>> probabilities = new HashMap<>();
	/**
	 * Storing the predictions probabilities, consists of classifications and its probabilities.
	 */
	Map<String, Double> predictions = new HashMap<>();
	/**
	 * Final prediction.
	 */
	String classification;

	public Result(Map<String, Map<String, Double>> probabilities, Map<String, Double> predictions) {
		this.probabilities = probabilities;
		this.predictions = predictions;
		this.classification = NaiveBayes.getHighestProbability(this.predictions);
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

	public static Result of(
		Map<String, Map<String, Double>> probabilities,
		Map<String, Double> predictions
	) {
		return new Result(probabilities, predictions);
	}
}