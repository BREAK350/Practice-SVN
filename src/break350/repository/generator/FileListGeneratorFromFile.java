package break350.repository.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class FileListGeneratorFromFile implements FileListGenerator {
	private File file;

	public FileListGeneratorFromFile(File file) {
		this.file = file;
	}

	@Override
	public List<File> generate(File root) {
		List<File> files = new ArrayList<File>();
		try {
			Reader r = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader reader = new BufferedReader(r);
			String row;
			while ((row = reader.readLine()) != null) {
				File f = new File(root, row);
				files.add(f);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}
}
