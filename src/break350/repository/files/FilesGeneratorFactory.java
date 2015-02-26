package break350.repository.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesGeneratorFactory {
	public static FilesGenerator getGenerator() {
		// File file = new File("file.txt");
		// return new FilesGeneratorFromFile(file);
		return new FilesGenerator() {

			@Override
			public List<File> generate(File root) {
				List<File> files = new ArrayList<File>();
				files.add(new File(root, "WebContent/javascript"));
				return files;
			}
		};
	}
}
