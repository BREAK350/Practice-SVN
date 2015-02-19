package break350.repository.files;

import java.util.ArrayList;
import java.util.List;

public class FilesGeneratorFactory {
	public static FilesGenerator getGenerator() {
		return new FilesGenerator() {

			@Override
			public List<String> generate() {
				List<String> list = new ArrayList<String>();
				list.add("");
				list.add("");
				list.add("");
				return list;
			}
		};
	}
}
