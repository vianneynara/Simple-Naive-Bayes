import java.nio.file.Paths;
import java.util.Map;

public class Main {

	public static void test() {
		CSVReader reader = new CSVReader();
		String pathToDataset = getPathToFile("dataset.txt");
		Dataset dataset = reader.readCSV(pathToDataset);

//		// print the dataset metadata
//		for (var feature : dataset.metadata().entrySet()) {
//			System.out.println("Feature: " + feature.getKey());
//			for (var frequencyEntry : feature.getValue().entrySet()) {
//				System.out.printf("%12s : %d %n", frequencyEntry.getKey(), frequencyEntry.getValue());
//			}
//			System.out.println("=".repeat(50));
//		}
//
//		System.out.println("\n".repeat(3));
//
		NaiveBayes nb = new NaiveBayes(dataset);
//
//		// print the correlations
//		Map<String, Map<String, Map<String, Integer>>> yCorrelations = nb.readCorrelations("buys_computer");
//		for (var yValuesEntry : yCorrelations.entrySet()) {
//			System.out.printf("[Y] [%s]: %n", yValuesEntry.getKey());
//			for (var xValuesEntry : yValuesEntry.getValue().entrySet()) {
//				System.out.printf("\t(X) (%s): %n", xValuesEntry.getKey());
//				for (var xValuesOccurrences : xValuesEntry.getValue().entrySet()) {
//					System.out.printf("\t\t- %-10s : %d %n", xValuesOccurrences.getKey(), xValuesOccurrences.getValue());
//				}
//			}
//			System.out.println("=".repeat(50));
//		}
//
//		double probability = nb.computeProbability(yCorrelations, "age", "31...40", "buys_computer", "yes");
//		System.out.printf("Probability: %.4f %n", probability);

		/* The actual test based on manual calculation */

//		Map<String, String> X = Map.of(
//			"age", "31...40",
//			"income", "low",
//			"student", "no",
//			"credit_rating", "fair"
//		);
		Map<String, String> X = Map.of(
			"age", ">40",
			"student", "no",
			"credit_rating", "excellent",
			"buys_computer", "no"
		);
		String target = "income";
		Result result = nb.predict(X, target, true);
		System.out.printf("Prediction Result (%s): %s (%.4f) [%.2f%%]",
			target,
			result.classification(),
			result.predictions().get(result.classification()),
			result.normalizedPredictions().get(result.classification()) * 100);
	}

	private static String getPathToFile(String fileName) {
		String pathToDataset;
		try {
			pathToDataset = ClassLoader.getSystemClassLoader().getResource(fileName).getPath();
		} catch (NullPointerException npe) {
			String currentDir = System.getProperty("user.dir");
			pathToDataset = Paths.get(currentDir, fileName).toString();
		}
		return pathToDataset;
	}

	public static void main(String[] args) {
		test();
	}
}
