package break350.repository.files;

import java.io.File;
import java.util.List;

public interface FilesGenerator {
	public List<File> generate(File root);
}
