package break350.repository.generator;

import java.io.File;

public class FileListFactory {
	public final static String FILE_NAME = "D:/Projects/Practic/Repository/test/files.txt";

	public static FileListGenerator getFileListGenerator() {
		File file = new File(FILE_NAME);
		return new FileListGeneratorFromFile(file);
	}
}
