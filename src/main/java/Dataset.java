import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Dataset {
	// features metadata, contains feature name and its values' frequency
	private LinkedHashMap<String, Map<String, Integer>> metadata;
	// list of each feature
	private List<String> headers;
	// list of each row, contains list of values
	private List<List<String>> data;

	public Dataset() {
		this.metadata = new LinkedHashMap<>();
		this.headers = new LinkedList<>();
		this.data = new LinkedList<>();
	}

	public LinkedHashMap<String, Map<String, Integer>> metadata() {
		return metadata;
	}

	public void setMetadata(LinkedHashMap<String, Map<String, Integer>> metadata) {
		this.metadata = metadata;
	}

	public List<String> headers() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public List<List<String>> data() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}
}