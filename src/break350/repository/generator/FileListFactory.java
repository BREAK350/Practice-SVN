package break350.repository.generator;

import java.io.File;

public class FileListFactory {
	public final static String FILE_NAME = "list.txt";

	public static FileListGenerator getFileListGenerator() {
		File file = new File(FILE_NAME);
		return new FileListGeneratorFromFile(file);
	}
}
