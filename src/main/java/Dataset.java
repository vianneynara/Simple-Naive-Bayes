import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Dataset {
	// features metadata, contains feature name and its values' frequency
	private LinkedHashMap<String, Map<String, Integer>> metadata;
	// list of each feature
	private List<String> features;
	// list of each row, contains list of values
	private List<List<String>> data;

	public Dataset() {
		this.metadata = new LinkedHashMap<>();
		this.features = new LinkedList<>();
		this.data = new LinkedList<>();
	}

	public LinkedHashMap<String, Map<String, Integer>> metadata() {
		return metadata;
	}

	public void setMetadata(LinkedHashMap<String, Map<String, Integer>> metadata) {
		this.metadata = metadata;
	}

	public List<String> features() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public List<List<String>> data() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}
}