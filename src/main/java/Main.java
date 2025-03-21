import java.util.Map;

public class Main {

	public static void main(String[] args) {
		String pathToDatset = ClassLoader.getSystemClassLoader().getResource("dataset.txt").getPath();

		CSVReader reader = new CSVReader();

		Dataset dataset = reader.readCSV(pathToDatset);

		// print the dataset metadata
		for (var feature : dataset.metadata().entrySet()) {
			System.out.println("Feature: " + feature.getKey());
			for (var frequencyEntry : feature.getValue().entrySet()) {
				System.out.printf("%12s : %d %n", frequencyEntry.getKey(), frequencyEntry.getValue());
			}
			System.out.println("=".repeat(50));
		}

		System.out.println("\n".repeat(3));

		NaiveBayes nb = new NaiveBayes(dataset);

		Map<String, Map<String, Map<String, Integer>>> yCorrelations = nb.readCorrelations("buys_computer");

		for (var yValuesEntry : yCorrelations.entrySet()) {
			System.out.printf("[Y] [%s]: %n", yValuesEntry.getKey());
			for (var xValuesEntry : yValuesEntry.getValue().entrySet()) {
				System.out.printf("\t(X) (%s): %n", xValuesEntry.getKey());
				for (var xValuesOccurrences : xValuesEntry.getValue().entrySet()) {
					System.out.printf("\t\t- %-10s : %d %n", xValuesOccurrences.getKey(), xValuesOccurrences.getValue());
				}
			}
			System.out.println("=".repeat(50));
		}
	}
}
