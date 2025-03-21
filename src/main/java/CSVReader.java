import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Simple Dataset reader to read String contents form a file, line by line.
 */
public class CSVReader {

	public CSVReader() {

	}

	public Dataset readCSV(String path) {
		return readCSV(path, ";");
	}

	public Dataset readCSV(String path, String separator) {
		return readCSV(path, separator, true);
	}

	public Dataset readCSV(String path, String separator, boolean hasHeader) {

		Dataset d = new Dataset();

		// checks whether the file's path exists
		File file = new File(path);
		if (!file.exists()) {
			throw new IllegalStateException("File path " + path + " does not exist");
		}

		int counter = -1; // helper debug util

		/* Reading line by line using BufferedReader */
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String currLine;
			boolean skippedFirstLine = false;

			while ((currLine = br.readLine()) != null) {
				// split the values
				String[] values = currLine.split(separator);

//				System.out.printf("Reading line %3d : %s %n", ++counter, Arrays.toString(values)); // debug

				// read first line
				if (!skippedFirstLine) {
					skippedFirstLine = true;
					if (hasHeader) {
						// use original header
						for (String value : values) {
							d.headers().add(value);
							d.metadata().put(value, new HashMap<>());
						}
						continue;
					} else {
						// create numberede header
						for (int i = 0; i < values.length; i++) {
							d.headers().add(String.valueOf(i));
							d.metadata().put(String.valueOf(i), new HashMap<>());
						}
					}
				}

				d.data().add(List.of(values));

				// process each values to the metadata.
				// get the feature, then count up for the value encountered
				for (int i = 0; i < d.headers().size(); i++) {
					// get the feature's value-frequency holder
					Map<String, Integer> feature = d.metadata().get(d.headers().get(i));

					// get the value of the last row inserted (current row)
					final String label = d.data().getLast().get(i);

					// update the frequency of that value in that feature
					feature.put(label, feature.getOrDefault(label, 0) + 1);
				}
			}
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}

		return d;
	}
}
