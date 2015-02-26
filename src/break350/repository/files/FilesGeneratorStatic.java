package break350.repository.files;

import java.util.ArrayList;
import java.util.List;

public class FilesGeneratorStatic implements FilesGenerator {

	@Override
	public List<String> generate() {
		List<String> files = new ArrayList<String>();
		files.add("WebContent/javascript/");
		return files;
	}

}
