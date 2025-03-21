public class Main {

	public static void main(String[] args) {
		String pathToDatset = ClassLoader.getSystemClassLoader().getResource("dataset.txt").getPath();

		CSVReader reader = new CSVReader();

		Dataset dataset = reader.readCSV(pathToDatset);

		// print the dataset metadata
		for (var feature : dataset.metadata().entrySet()) {
			System.out.println("Feature: " + feature.getKey());
			for (var frequencyEntry : feature.getValue().entrySet()) {
				System.out.printf("%s : %d %n", frequencyEntry.getKey(), frequencyEntry.getValue());
			}
			System.out.println("=".repeat(50));
		}
	}
}
