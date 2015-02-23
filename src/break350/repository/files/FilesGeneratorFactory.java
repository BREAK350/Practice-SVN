package break350.repository.files;

import java.io.File;

public class FilesGeneratorFactory {
	public static FilesGenerator getGenerator() {
		File file = new File("file.txt");
		return new FilesGeneratorFromFile(file);
	}
}
